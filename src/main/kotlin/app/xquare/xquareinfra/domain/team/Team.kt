package app.xquare.xquareinfra.domain.team

data class Team(
    val id: Long? = null,
    val name: String,
    val type: TeamType,
    val members: List<TeamMember>,
) {
    fun isMember(userId: Long): Boolean = members.any { it.user.id == userId }

    fun isAdmin(userId: Long): Boolean = members.any { it.user.id == userId && it.role == TeamMemberRole.ADMIN }
}
