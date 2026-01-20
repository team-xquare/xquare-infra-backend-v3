package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.user.User

data class DeleteMembersCommand(
    val user: User,
    val teamId: Long,
    val memberIds: List<Long>,
)

data object DeleteMembersResult

interface DeleteMembersUseCase {
    fun deleteMembers(command: DeleteMembersCommand): DeleteMembersResult
}
