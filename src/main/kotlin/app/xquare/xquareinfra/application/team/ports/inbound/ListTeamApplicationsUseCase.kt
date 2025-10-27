package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.application.Application

data class ListTeamApplicationsQuery(
    val userId: Long,
    val teamId: Long,
)

data class ListTeamApplicationsResult(
    val applications: List<Application>,
)

interface ListTeamApplicationsUseCase {
    fun listTeamApplications(query: ListTeamApplicationsQuery): ListTeamApplicationsResult
}
