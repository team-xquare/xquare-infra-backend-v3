package app.xquare.xquareinfra.application.team.ports.inbound

data class DeleteMembersCommand(
    val userId: Long,
    val teamId: Long,
    val memberIds: List<Long>,
)

sealed class DeleteMembersResult {
    data object Success : DeleteMembersResult()

    data object TeamNotFound : DeleteMembersResult()

    data object NotAdmin : DeleteMembersResult()
}

interface DeleteMembersUseCase {
    fun deleteMembers(command: DeleteMembersCommand): DeleteMembersResult
}
