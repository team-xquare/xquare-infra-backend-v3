package app.xquare.xquareinfra.application.team

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
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamsQuery
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamsResult
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamsUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.UpdateTeamCommand
import app.xquare.xquareinfra.application.team.ports.inbound.UpdateTeamResult
import app.xquare.xquareinfra.application.team.ports.inbound.UpdateTeamUseCase
import app.xquare.xquareinfra.application.team.ports.outbound.ApplicationPersistencePort
import app.xquare.xquareinfra.application.team.ports.outbound.TeamPersistencePort
import app.xquare.xquareinfra.application.team.ports.outbound.UserPersistenceForTeamPort
import app.xquare.xquareinfra.domain.team.Team
import app.xquare.xquareinfra.domain.team.TeamMember
import app.xquare.xquareinfra.domain.team.TeamMemberRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class TeamService(
    private val teamPersistencePort: TeamPersistencePort,
    private val userPersistencePort: UserPersistenceForTeamPort,
    private val applicationPersistencePort: ApplicationPersistencePort,
) : CreateTeamUseCase,
    ListTeamsUseCase,
    GetTeamUseCase,
    AddOrUpdateMembersUseCase,
    DeleteMembersUseCase,
    UpdateTeamUseCase,
    DeleteTeamUseCase {
    override fun listTeams(query: ListTeamsQuery): ListTeamsResult {
        val teams = teamPersistencePort.listByUserId(query.userId)
        return ListTeamsResult.Success(teams)
    }

    override fun getTeam(query: GetTeamQuery): GetTeamResult {
        val team = teamPersistencePort.findById(query.teamId) ?: return GetTeamResult.TeamNotFound

        if (!team.members.any({ it.user.id == query.userId })) {
            return GetTeamResult.NotTeamMember
        }

        return GetTeamResult.Success(team)
    }

    override fun createTeam(command: CreateTeamCommand): CreateTeamResult {
        if (teamPersistencePort.existsByName(command.name)) {
            return CreateTeamResult.TeamNameAlreadyExists
        }

        val membersToAdd =
            (
                command.initialMembers.filterNot { it.memberId != command.userId } +
                    CreateTeamCommand.InitialMember(memberId = command.userId, role = TeamMemberRole.ADMIN)
            ).distinctBy { it.memberId }
                .map {
                    val user = userPersistencePort.findById(it.memberId) ?: return CreateTeamResult.UserNotFound
                    TeamMember(user = user, role = it.role)
                }

        val team =
            Team(
                name = command.name,
                type = command.type,
                members = membersToAdd,
            )

        val savedTeam = teamPersistencePort.save(team)
        return CreateTeamResult.Success(teamId = savedTeam.id!!)
    }

    override fun addOrUpdateMembers(command: AddOrUpdateMembersCommand): AddOrUpdateMembersResult {
        val team = teamPersistencePort.findById(command.teamId) ?: return AddOrUpdateMembersResult.TeamNotFound
        if (!isAdmin(team, command.userId)) return AddOrUpdateMembersResult.NotAdmin

        val newRoleMap = command.members.associate { it.memberId to it.role }
        val updatedMembers = team.members.map { it.copy(role = newRoleMap[it.user.id] ?: it.role) }

        val existingIdsSet = team.members.map { it.user.id!! }.toSet()
        val newMembers =
            command.members
                .filterNot { it.memberId in existingIdsSet }
                .map {
                    val user = userPersistencePort.findById(it.memberId) ?: return AddOrUpdateMembersResult.UserNotFound
                    TeamMember(user = user, role = it.role)
                }

        val updatedTeam = team.copy(members = updatedMembers + newMembers)
        teamPersistencePort.save(updatedTeam)

        return AddOrUpdateMembersResult.Success
    }

    override fun updateTeam(command: UpdateTeamCommand): UpdateTeamResult {
        val team = teamPersistencePort.findById(command.teamId) ?: return UpdateTeamResult.TeamNotFound
        if (!isAdmin(team, command.userId)) return UpdateTeamResult.NotAdmin

        val updatedTeam = team.copy(name = command.name ?: team.name, type = command.type ?: team.type)
        teamPersistencePort.save(updatedTeam)

        return UpdateTeamResult.Success
    }

    override fun deleteMembers(command: DeleteMembersCommand): DeleteMembersResult {
        val team = teamPersistencePort.findById(command.teamId) ?: return DeleteMembersResult.TeamNotFound
        if (!isAdmin(team, command.userId)) return DeleteMembersResult.NotAdmin

        val deletedIdsSet = command.memberIds.toSet()

        val updatedTeam = team.copy(members = team.members.filter { it.user.id !in deletedIdsSet })
        teamPersistencePort.save(updatedTeam)

        return DeleteMembersResult.Success
    }

    override fun deleteTeam(command: DeleteTeamCommand): DeleteTeamResult {
        val team = teamPersistencePort.findById(command.teamId) ?: return DeleteTeamResult.TeamNotFound
        if (!isAdmin(team, command.userId)) return DeleteTeamResult.NotAdmin

        applicationPersistencePort.deleteByTeamId(command.teamId)
        teamPersistencePort.delete(command.teamId)

        return DeleteTeamResult.Success
    }

    private fun isAdmin(
        team: Team,
        userId: Long,
    ): Boolean = team.members.any { it.user.id == userId && it.role == TeamMemberRole.ADMIN }
}
