package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.team.Team
import app.xquare.xquareinfra.domain.user.User

data class GetTeamQuery(
    val user: User,
    val teamId: Long,
)

data class GetTeamResult(
    val team: Team,
)

interface GetTeamUseCase {
    fun getTeam(query: GetTeamQuery): GetTeamResult
}
