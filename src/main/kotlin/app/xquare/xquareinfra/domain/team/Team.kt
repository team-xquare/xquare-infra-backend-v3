package app.xquare.xquareinfra.domain.team

data class Team(
    val id: Long? = null,
    val name: String,
    val type: TeamType,
    val members: List<TeamMember>,
)
