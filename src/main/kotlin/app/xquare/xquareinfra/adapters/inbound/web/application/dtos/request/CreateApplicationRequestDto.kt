package app.xquare.xquareinfra.adapters.inbound.web.application.dtos.request

import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration.ApplicationConfigurationDto

data class CreateApplicationRequestDto(
    val teamId: Long,
    val name: String,
    val configuration: ApplicationConfigurationDto,
)
