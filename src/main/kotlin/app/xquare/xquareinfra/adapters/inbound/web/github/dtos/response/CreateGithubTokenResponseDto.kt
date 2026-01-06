package app.xquare.xquareinfra.adapters.inbound.web.github.dtos.response

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class CreateGithubTokenResponseDto(
    val accessToken: String,
    val tokenType: String,
    val scope: String? = null,
    val expiresIn: Long? = null,
    val refreshToken: String? = null,
    val refreshTokenExpiresIn: Long? = null,
) : SuccessResponseDto
