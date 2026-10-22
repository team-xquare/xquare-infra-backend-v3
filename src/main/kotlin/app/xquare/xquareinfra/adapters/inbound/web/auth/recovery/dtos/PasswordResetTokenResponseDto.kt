package app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto
import io.swagger.v3.oas.annotations.media.Schema

/**
 * Returns the opaque, short-lived credential required to reset a password.
 *
 * The token lifetime follows the configured verified-token TTL. Clients must treat it as confidential,
 * avoid logging or persisting it, and transmit it only over HTTPS.
 */
data class PasswordResetTokenResponseDto(
    /** Opaque token supplied once to the password reset endpoint before it expires. */
    @field:Schema(
        description = "비밀번호 재설정 요청에 제출해야 하는 단기 재설정 토큰",
        example = "7f4f7c50-4b53-4b63-a76d-625deea5b61f",
    )
    val passwordResetToken: String,
) : SuccessResponseDto
