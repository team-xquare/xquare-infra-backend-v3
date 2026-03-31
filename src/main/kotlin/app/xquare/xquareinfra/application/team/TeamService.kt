package app.xquare.xquareinfra.application.team

import app.xquare.xquareinfra.application.application.ApplicationException
import app.xquare.xquareinfra.application.application.ports.outbound.ApplicationConfigurationPublishPort
import app.xquare.xquareinfra.application.global.exception.CommonException
import app.xquare.xquareinfra.application.team.ports.inbound.*
import app.xquare.xquareinfra.application.team.ports.outbound.AddonPersistenceForTeamPort
import app.xquare.xquareinfra.application.team.ports.outbound.ApplicationPersistenceForTeamPort
import app.xquare.xquareinfra.application.team.ports.outbound.TeamPersistenceForTeamPort
import app.xquare.xquareinfra.application.team.ports.outbound.UserPersistenceForTeamPort
import app.xquare.xquareinfra.domain.application.ApplicationStatus
import app.xquare.xquareinfra.domain.team.Team
import app.xquare.xquareinfra.domain.team.TeamMember
import app.xquare.xquareinfra.domain.team.TeamMemberRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class TeamService(
    private val teamPersistencePort: TeamPersistenceForTeamPort,
    private val userPersistencePort: UserPersistenceForTeamPort,
    private val applicationPersistencePort: ApplicationPersistenceForTeamPort,
    private val applicationConfigurationPublishPort: ApplicationConfigurationPublishPort,
    private val addonPersistencePort: AddonPersistenceForTeamPort,
) : CreateTeamUseCase,
    ListUserTeamsUseCase,
    GetTeamUseCase,
    ListTeamApplicationsUseCase,
    ListTeamAddonsUseCase,
    AddOrUpdateMembersUseCase,
    DeleteMembersUseCase,
    UpdateTeamUseCase,
    DeleteTeamUseCase {
    override fun listUserTeams(query: ListUserTeamsQuery): ListUserTeamsResult {
        val teams = teamPersistencePort.listByUserId(query.user.id!!)
        return ListUserTeamsResult(teams)
    }

    override fun getTeam(query: GetTeamQuery): GetTeamResult {
        val team = teamPersistencePort.findById(query.teamId) ?: throw CommonException.TeamNotFound

        if (!team.isMember(query.user)) {
            throw CommonException.NotTeamMember
        }

        return GetTeamResult(team)
    }

    override fun createTeam(command: CreateTeamCommand): CreateTeamResult {
        if (teamPersistencePort.existsByName(command.name)) {
            throw TeamException.TeamNameAlreadyExists
        }

        val membersToAdd =
            (
                command.initialMembers.filterNot { it.memberId != command.userId } +
                    CreateTeamCommand.InitialMember(memberId = command.userId, role = TeamMemberRole.ADMIN)
            ).distinctBy { it.memberId }
                .map {
                    val user = userPersistencePort.findById(it.memberId) ?: throw CommonException.UserNotFound
                    TeamMember(user = user, role = it.role)
                }

        val team =
            Team(
                name = command.name,
                type = command.type,
                members = membersToAdd,
            )

        val savedTeam = teamPersistencePort.save(team)
        return CreateTeamResult(teamId = savedTeam.id!!)
    }

    override fun listTeamApplications(query: ListTeamApplicationsQuery): ListTeamApplicationsResult {
        val team = teamPersistencePort.findById(query.teamId) ?: throw CommonException.TeamNotFound

        if (!team.isMember(query.user)) {
            throw CommonException.NotTeamMember
        }

        val applications =
            applicationPersistencePort.listByTeamId(query.teamId).map lit@{ application ->
                if (application.status != ApplicationStatus.PUBLISHED) {
                    return@lit application
                }

                val configuration =
                    applicationConfigurationPublishPort
                        .getPublishedConfiguration(application.team.name, application.name)
                        ?: throw ApplicationException.FailedToFetchConfiguration

                return@lit application.copy(configuration = configuration)
            }

        return ListTeamApplicationsResult(applications)
    }

    override fun listTeamAddons(query: ListTeamAddonsQuery): ListTeamAddonsResult {
        val team =
            teamPersistencePort.findById(query.teamId)
                ?: throw CommonException.TeamNotFound

        if (!team.isMember(query.user)) {
            throw CommonException.NotTeamMember
        }

        val addons = addonPersistencePort.listByTeamId(query.teamId)
        return ListTeamAddonsResult(addons)
    }

    override fun addOrUpdateMembers(command: AddOrUpdateMembersCommand): AddOrUpdateMembersResult {
        val team = teamPersistencePort.findById(command.teamId) ?: throw CommonException.TeamNotFound
        if (!team.isAdmin(command.user)) throw TeamException.NotTeamAdmin

        val newRoleMap = command.members.associate { it.memberId to it.role }
        val updatedMembers = team.members.map { it.copy(role = newRoleMap[it.user.id] ?: it.role) }

        val existingIdsSet = team.members.map { it.user.id!! }.toSet()
        val newMembers =
            command.members
                .filterNot { it.memberId in existingIdsSet }
                .map {
                    val user = userPersistencePort.findById(it.memberId) ?: throw CommonException.UserNotFound
                    TeamMember(user = user, role = it.role)
                }

        val updatedTeam = team.copy(members = updatedMembers + newMembers)
        teamPersistencePort.save(updatedTeam)

        return AddOrUpdateMembersResult
    }

    override fun updateTeam(command: UpdateTeamCommand): UpdateTeamResult {
        val team = teamPersistencePort.findById(command.teamId) ?: throw CommonException.TeamNotFound
        if (!team.isAdmin(command.user)) throw TeamException.NotTeamAdmin

        val updatedTeam = team.copy(type = command.type ?: team.type)
        teamPersistencePort.save(updatedTeam)

        return UpdateTeamResult
    }

    override fun deleteMembers(command: DeleteMembersCommand): DeleteMembersResult {
        val team = teamPersistencePort.findById(command.teamId) ?: throw CommonException.TeamNotFound
        if (!team.isAdmin(command.user)) throw TeamException.NotTeamAdmin

        val deletedIdsSet = command.memberIds.toSet()

        val updatedTeam = team.copy(members = team.members.filter { it.user.id !in deletedIdsSet })
        teamPersistencePort.save(updatedTeam)

        return DeleteMembersResult
    }

    override fun deleteTeam(command: DeleteTeamCommand): DeleteTeamResult {
        val team = teamPersistencePort.findById(command.teamId) ?: throw CommonException.TeamNotFound
        if (!team.isAdmin(command.user)) throw TeamException.NotTeamAdmin

        applicationPersistencePort.deleteByTeamId(command.teamId)
        teamPersistencePort.delete(command.teamId)

        return DeleteTeamResult
    }
}
