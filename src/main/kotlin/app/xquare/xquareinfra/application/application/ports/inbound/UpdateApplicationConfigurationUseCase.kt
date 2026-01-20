package app.xquare.xquareinfra.application.application.ports.inbound

import app.xquare.xquareinfra.domain.application.ApplicationConfiguration
import app.xquare.xquareinfra.domain.user.User

data class UpdateApplicationConfigurationCommand(
    val user: User,
    val applicationId: Long,
    val configuration: ApplicationConfiguration,
)

data object UpdateApplicationConfigurationResult

interface UpdateApplicationConfigurationUseCase {
    fun updateApplicationConfiguration(command: UpdateApplicationConfigurationCommand): UpdateApplicationConfigurationResult
}
