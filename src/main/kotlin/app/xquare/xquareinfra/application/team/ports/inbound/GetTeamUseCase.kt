package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.team.Team

data class GetTeamQuery(
    val userId: Long,
    val teamId: Long,
)

data class GetTeamResult(
    val team: Team,
)

interface GetTeamUseCase {
    fun getTeam(query: GetTeamQuery): GetTeamResult
}
