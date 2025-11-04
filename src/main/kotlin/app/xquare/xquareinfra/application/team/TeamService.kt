package app.xquare.xquareinfra.application.team

import app.xquare.xquareinfra.application.global.exception.CommonException
import app.xquare.xquareinfra.application.team.ports.inbound.AddOrUpdateMembersCommand
import app.xquare.xquareinfra.application.team.ports.inbound.AddOrUpdateMembersResult
import app.xquare.xquareinfra.application.team.ports.inbound.AddOrUpdateMembersUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.CreateTeamCommand
import app.xquare.xquareinfra.application.team.ports.inbound.CreateTeamResult
import app.xquare.xquareinfra.application.team.ports.inbound.CreateTeamUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.DeleteMembersCommand
import app.xquare.xquareinfra.application.team.ports.inbound.DeleteMembersResult
import app.xquare.xquareinfra.application.team.ports.inbound.DeleteMembersUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.DeleteTeamCommand
import app.xquare.xquareinfra.application.team.ports.inbound.DeleteTeamResult
import app.xquare.xquareinfra.application.team.ports.inbound.DeleteTeamUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.GetTeamQuery
import app.xquare.xquareinfra.application.team.ports.inbound.GetTeamResult
import app.xquare.xquareinfra.application.team.ports.inbound.GetTeamUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamAddonsQuery
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamAddonsResult
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamAddonsUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamApplicationsQuery
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamApplicationsResult
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamApplicationsUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamsQuery
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamsResult
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamsUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.UpdateTeamCommand
import app.xquare.xquareinfra.application.team.ports.inbound.UpdateTeamResult
import app.xquare.xquareinfra.application.team.ports.inbound.UpdateTeamUseCase
import app.xquare.xquareinfra.application.team.ports.outbound.AddonPersistenceForTeamPort
import app.xquare.xquareinfra.application.team.ports.outbound.ApplicationPersistenceForTeamPort
import app.xquare.xquareinfra.application.team.ports.outbound.TeamPersistenceForTeamPort
import app.xquare.xquareinfra.application.team.ports.outbound.UserPersistenceForTeamPort
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
    private val addonPersistencePort: AddonPersistenceForTeamPort,
) : CreateTeamUseCase,
    ListTeamsUseCase,
    GetTeamUseCase,
    ListTeamApplicationsUseCase,
    ListTeamAddonsUseCase,
    AddOrUpdateMembersUseCase,
    DeleteMembersUseCase,
    UpdateTeamUseCase,
    DeleteTeamUseCase {
    override fun listTeams(query: ListTeamsQuery): ListTeamsResult {
        val teams = teamPersistencePort.listByUserId(query.userId)
        return ListTeamsResult(teams)
    }

    override fun getTeam(query: GetTeamQuery): GetTeamResult {
        val team = teamPersistencePort.findById(query.teamId) ?: throw CommonException.TeamNotFound

        if (!team.isMember(query.userId)) {
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

        if (!team.isMember(query.userId)) {
            throw CommonException.NotTeamMember
        }

        val applications = applicationPersistencePort.listByTeamId(query.teamId)
        return ListTeamApplicationsResult(applications)
    }

    override fun listTeamAddons(query: ListTeamAddonsQuery): ListTeamAddonsResult {
        val team =
            teamPersistencePort.findById(query.teamId)
                ?: throw CommonException.TeamNotFound

        if (!team.isMember(query.userId)) {
            throw CommonException.NotTeamMember
        }

        val addons = addonPersistencePort.listByTeamId(query.teamId)
        return ListTeamAddonsResult(addons)
    }

    override fun addOrUpdateMembers(command: AddOrUpdateMembersCommand): AddOrUpdateMembersResult {
        val team = teamPersistencePort.findById(command.teamId) ?: throw CommonException.TeamNotFound
        if (!team.isAdmin(command.userId)) throw TeamException.NotTeamAdmin

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
        if (!team.isAdmin(command.userId)) throw TeamException.NotTeamAdmin

        val updatedTeam = team.copy(type = command.type ?: team.type)
        teamPersistencePort.save(updatedTeam)

        return UpdateTeamResult
    }

    override fun deleteMembers(command: DeleteMembersCommand): DeleteMembersResult {
        val team = teamPersistencePort.findById(command.teamId) ?: throw CommonException.TeamNotFound
        if (!team.isAdmin(command.userId)) throw TeamException.NotTeamAdmin

        val deletedIdsSet = command.memberIds.toSet()

        val updatedTeam = team.copy(members = team.members.filter { it.user.id !in deletedIdsSet })
        teamPersistencePort.save(updatedTeam)

        return DeleteMembersResult
    }

    override fun deleteTeam(command: DeleteTeamCommand): DeleteTeamResult {
        val team = teamPersistencePort.findById(command.teamId) ?: throw CommonException.TeamNotFound
        if (!team.isAdmin(command.userId)) throw TeamException.NotTeamAdmin

        applicationPersistencePort.deleteByTeamId(command.teamId)
        teamPersistencePort.delete(command.teamId)

        return DeleteTeamResult
    }
}
