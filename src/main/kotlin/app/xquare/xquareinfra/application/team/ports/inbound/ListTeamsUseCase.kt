package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.team.Team

data class ListTeamsQuery(
    val userId: Long,
)

data class ListTeamsResult(
    val teams: List<Team>,
)

interface ListTeamsUseCase {
    fun listTeams(query: ListTeamsQuery): ListTeamsResult
}
