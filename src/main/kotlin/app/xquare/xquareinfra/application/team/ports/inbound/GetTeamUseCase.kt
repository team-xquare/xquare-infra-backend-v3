package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.team.Team

data class GetTeamQuery(
    val userId: Long,
    val teamId: Long,
)

sealed class GetTeamResult {
    data class Success(
        val team: Team,
    ) : GetTeamResult()

    data object TeamNotFound : GetTeamResult()

    data object NotTeamMember : GetTeamResult()
}

interface GetTeamUseCase {
    fun getTeam(query: GetTeamQuery): GetTeamResult
}
