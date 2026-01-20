package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.team.Team
import app.xquare.xquareinfra.domain.user.User

data class ListUserTeamsQuery(
    val user: User,
)

data class ListUserTeamsResult(
    val teams: List<Team>,
)

interface ListUserTeamsUseCase {
    fun listUserTeams(query: ListUserTeamsQuery): ListUserTeamsResult
}
