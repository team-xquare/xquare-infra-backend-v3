package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.team.TeamType
import app.xquare.xquareinfra.domain.user.User

data class UpdateTeamCommand(
    val user: User,
    val teamId: Long,
    val type: TeamType?,
)

data object UpdateTeamResult

interface UpdateTeamUseCase {
    fun updateTeam(command: UpdateTeamCommand): UpdateTeamResult
}
