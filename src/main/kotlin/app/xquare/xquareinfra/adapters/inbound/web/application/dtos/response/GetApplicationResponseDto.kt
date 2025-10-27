package app.xquare.xquareinfra.adapters.inbound.web.application.dtos.response

import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.ApplicationStatusDto
import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration.ApplicationConfigurationDto
import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class GetApplicationResponseDto(
    val id: Long,
    val teamId: Long,
    val name: String,
    val status: ApplicationStatusDto,
    val configuration: ApplicationConfigurationDto,
) : SuccessResponseDto
