package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.team.TeamMemberRole
import app.xquare.xquareinfra.domain.user.User

data class AddOrUpdateMembersCommand(
    val user: User,
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
