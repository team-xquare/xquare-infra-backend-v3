package app.xquare.xquareinfra.application.application.ports.inbound

import app.xquare.xquareinfra.domain.application.ApplicationConfiguration

data class CreateApplicationCommand(
    val userId: Long,
    val teamId: Long,
    val name: String,
    val configuration: ApplicationConfiguration,
)

data class CreateApplicationResult(
    val applicationId: Long,
)

interface CreateApplicationUseCase {
    fun createApplication(command: CreateApplicationCommand): CreateApplicationResult
}
