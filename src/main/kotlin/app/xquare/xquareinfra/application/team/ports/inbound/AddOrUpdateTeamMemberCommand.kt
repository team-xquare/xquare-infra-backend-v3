package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.team.TeamMemberRole

data class AddOrUpdateMembersCommand(
    val userId: Long,
    val teamId: Long,
    val members: List<AddOrUpdateMember>,
) {
    data class AddOrUpdateMember(
        val memberId: Long,
        val role: TeamMemberRole,
    )
}

sealed class AddOrUpdateMembersResult {
    data object Success : AddOrUpdateMembersResult()

    data object TeamNotFound : AddOrUpdateMembersResult()

    data object NotAdmin : AddOrUpdateMembersResult()

    data object UserNotFound : AddOrUpdateMembersResult()
}

interface AddOrUpdateMembersUseCase {
    fun addOrUpdateMembers(command: AddOrUpdateMembersCommand): AddOrUpdateMembersResult
}
