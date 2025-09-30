package app.xquare.xquareinfra.application.team.ports.inbound

data class DeleteTeamCommand(
    val userId: Long,
    val teamId: Long,
)

sealed class DeleteTeamResult {
    data object Success : DeleteTeamResult()

    data object TeamNotFound : DeleteTeamResult()

    data object NotAdmin : DeleteTeamResult()
}

interface DeleteTeamUseCase {
    fun deleteTeam(command: DeleteTeamCommand): DeleteTeamResult
}
