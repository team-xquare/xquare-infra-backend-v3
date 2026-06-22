package app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ResetPasswordRequestDto(
    @field:NotBlank
    val passwordResetToken: String,
    @field:Size(min = 8, max = 20)
    @field:Pattern(
        regexp = ".*[!@#\$%^&*(),.?\":{}|<>].*",
    )
    val newPassword: String,
)
