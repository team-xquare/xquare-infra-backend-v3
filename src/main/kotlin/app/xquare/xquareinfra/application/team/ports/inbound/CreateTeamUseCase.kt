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

data class CreateTeamResult(
    val teamId: Long,
)

interface CreateTeamUseCase {
    fun createTeam(command: CreateTeamCommand): CreateTeamResult
}
