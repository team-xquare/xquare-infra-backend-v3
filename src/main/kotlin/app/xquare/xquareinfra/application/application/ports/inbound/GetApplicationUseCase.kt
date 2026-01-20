package app.xquare.xquareinfra.application.application.ports.inbound

import app.xquare.xquareinfra.domain.application.Application
import app.xquare.xquareinfra.domain.user.User

data class GetApplicationQuery(
    val user: User,
    val applicationId: Long,
)

data class GetApplicationResult(
    val application: Application,
)

interface GetApplicationUseCase {
    fun getApplication(query: GetApplicationQuery): GetApplicationResult
}
