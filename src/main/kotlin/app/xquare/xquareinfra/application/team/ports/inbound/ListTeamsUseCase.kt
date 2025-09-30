package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.team.Team

data class ListTeamsQuery(
    val userId: Long,
)

sealed class ListTeamsResult {
    data class Success(
        val teams: List<Team>,
    ) : ListTeamsResult()
}

interface ListTeamsUseCase {
    fun listTeams(query: ListTeamsQuery): ListTeamsResult
}
