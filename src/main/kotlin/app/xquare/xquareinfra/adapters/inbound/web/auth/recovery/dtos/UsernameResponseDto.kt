package app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class UsernameResponseDto(
    val username: String,
) : SuccessResponseDto
