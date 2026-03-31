package app.xquare.xquareinfra.adapters.inbound.web.auth.dtos

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class EmailVerifiedTokenResponseDto (
    val emailVerifiedToken: String,
): SuccessResponseDto
