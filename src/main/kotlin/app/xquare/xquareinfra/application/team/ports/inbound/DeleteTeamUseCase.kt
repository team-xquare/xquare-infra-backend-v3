package app.xquare.xquareinfra.application.team.ports.inbound

data class DeleteTeamCommand(
    val userId: Long,
    val teamId: Long,
)

data object DeleteTeamResult

interface DeleteTeamUseCase {
    fun deleteTeam(command: DeleteTeamCommand): DeleteTeamResult
}
