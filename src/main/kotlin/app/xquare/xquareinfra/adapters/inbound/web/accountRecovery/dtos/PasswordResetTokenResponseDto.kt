package app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class PasswordResetTokenResponseDto(
    val passwordResetToken: String,
) : SuccessResponseDto
