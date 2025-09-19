package app.xquare.xquareinfra.adapters.inbound.web.auth

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class TokenResponseDto(
    val accessToken: String,
    val refreshToken: String,
) : SuccessResponseDto
