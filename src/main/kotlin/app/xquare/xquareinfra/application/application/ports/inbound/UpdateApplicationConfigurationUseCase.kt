package app.xquare.xquareinfra.application.application.ports.inbound

import app.xquare.xquareinfra.domain.application.ApplicationConfiguration

data class UpdateApplicationConfigurationCommand(
    val userId: Long,
    val applicationId: Long,
    val configuration: ApplicationConfiguration,
)

data object UpdateApplicationConfigurationResult

interface UpdateApplicationConfigurationUseCase {
    fun updateApplicationConfiguration(command: UpdateApplicationConfigurationCommand): UpdateApplicationConfigurationResult
}
