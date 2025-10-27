package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.team.TeamType

data class UpdateTeamCommand(
    val userId: Long,
    val teamId: Long,
    val type: TeamType?,
)

data object UpdateTeamResult

interface UpdateTeamUseCase {
    fun updateTeam(command: UpdateTeamCommand): UpdateTeamResult
}
