package app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

/**
 * Returns the opaque, short-lived credential required to reset a password.
 *
 * The token lifetime follows the configured verified-token TTL. Clients must treat it as confidential,
 * avoid logging or persisting it, and transmit it only over HTTPS.
 */
data class PasswordResetTokenResponseDto(
    /** Opaque token supplied once to the password reset endpoint before it expires. */
    val passwordResetToken: String,
) : SuccessResponseDto
