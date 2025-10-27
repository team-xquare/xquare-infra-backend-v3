package app.xquare.xquareinfra.adapters.inbound.web.application.dtos.response

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class CreateApplicationResponseDto(
    val applicationId: Long,
) : SuccessResponseDto
