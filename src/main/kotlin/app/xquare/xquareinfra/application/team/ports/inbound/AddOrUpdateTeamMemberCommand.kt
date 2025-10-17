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

data object AddOrUpdateMembersResult

interface AddOrUpdateMembersUseCase {
    fun addOrUpdateMembers(command: AddOrUpdateMembersCommand): AddOrUpdateMembersResult
}
