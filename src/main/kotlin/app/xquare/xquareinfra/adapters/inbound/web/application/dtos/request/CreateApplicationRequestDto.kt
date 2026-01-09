package app.xquare.xquareinfra.adapters.inbound.web.application.dtos.request

import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration.ApplicationConfigurationDto
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateApplicationRequestDto(
    val teamId: Long,
    @field:Size(min = 3, max = 45)
    @field:Pattern(regexp = "^[a-z-]+$")
    val name: String,
    val configuration: ApplicationConfigurationDto,
)
