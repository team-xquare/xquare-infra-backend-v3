package app.xquare.xquareinfra.application.team.ports.inbound

data class DeleteMembersCommand(
    val userId: Long,
    val teamId: Long,
    val memberIds: List<Long>,
)

data object DeleteMembersResult

interface DeleteMembersUseCase {
    fun deleteMembers(command: DeleteMembersCommand): DeleteMembersResult
}
