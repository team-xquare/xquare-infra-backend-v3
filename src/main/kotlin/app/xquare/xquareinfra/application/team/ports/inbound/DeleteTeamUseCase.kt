package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.user.User

data class DeleteTeamCommand(
    val user: User,
    val teamId: Long,
)

data object DeleteTeamResult

interface DeleteTeamUseCase {
    fun deleteTeam(command: DeleteTeamCommand): DeleteTeamResult
}
