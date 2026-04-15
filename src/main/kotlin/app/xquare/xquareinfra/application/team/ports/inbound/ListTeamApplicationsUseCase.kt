package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.application.Application
import app.xquare.xquareinfra.domain.user.User

data class ListTeamApplicationsQuery(
    val user: User,
    val teamId: Long,
)

data class ListTeamApplicationsResult(
    val applications: List<Application>,
)

interface ListTeamApplicationsUseCase {
    suspend fun listTeamApplications(query: ListTeamApplicationsQuery): ListTeamApplicationsResult
}
