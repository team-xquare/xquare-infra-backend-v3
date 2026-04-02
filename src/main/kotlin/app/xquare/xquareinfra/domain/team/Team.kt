package app.xquare.xquareinfra.domain.team

import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.domain.user.UserRole

data class Team(
    val id: Long? = null,
    val name: String,
    val type: TeamType,
    val members: List<TeamMember>,
) {
    fun isMember(user: User): Boolean = user.role == UserRole.ADMIN || members.any { it.user.id == user.id }

    fun isAdmin(user: User): Boolean =
        user.role == UserRole.ADMIN ||
            members.any { it.user.id == user.id && it.role == TeamMemberRole.ADMIN }
}
