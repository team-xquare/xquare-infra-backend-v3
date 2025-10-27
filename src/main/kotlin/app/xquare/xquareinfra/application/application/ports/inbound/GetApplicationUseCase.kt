package app.xquare.xquareinfra.application.application.ports.inbound

import app.xquare.xquareinfra.domain.application.Application

data class GetApplicationQuery(
    val userId: Long,
    val applicationId: Long,
)

data class GetApplicationResult(
    val application: Application,
)

interface GetApplicationUseCase {
    fun getApplication(query: GetApplicationQuery): GetApplicationResult
}
