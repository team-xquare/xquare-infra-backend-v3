package app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common

import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration.ApplicationConfigurationDto

data class UpdateApplicationConfigurationRequestDto(
    val configuration: ApplicationConfigurationDto,
)
