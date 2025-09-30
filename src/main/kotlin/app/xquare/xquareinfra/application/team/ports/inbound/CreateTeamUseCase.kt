package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.team.TeamMemberRole
import app.xquare.xquareinfra.domain.team.TeamType

data class CreateTeamCommand(
    val userId: Long,
    val name: String,
    val type: TeamType,
    val initialMembers: List<InitialMember>,
) {
    data class InitialMember(
        val memberId: Long,
        val role: TeamMemberRole,
    )
}

sealed class CreateTeamResult {
    data class Success(
        val teamId: Long,
    ) : CreateTeamResult()

    data object TeamNameAlreadyExists : CreateTeamResult()

    data object UserNotFound : CreateTeamResult()
}

interface CreateTeamUseCase {
    fun createTeam(command: CreateTeamCommand): CreateTeamResult
}
