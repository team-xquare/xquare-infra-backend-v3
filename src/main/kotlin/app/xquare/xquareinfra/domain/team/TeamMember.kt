package app.xquare.xquareinfra.domain.team

import app.xquare.xquareinfra.domain.user.User

data class TeamMember(
    val user: User,
    val role: TeamMemberRole,
)
