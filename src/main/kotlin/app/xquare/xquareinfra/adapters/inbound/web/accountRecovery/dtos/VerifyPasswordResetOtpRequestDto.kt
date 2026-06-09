package app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class VerifyPasswordResetOtpRequestDto(
    @field:Size(min = 4, max = 15)
    @field:Pattern(
        regexp = "^[A-Za-z0-9_-]+$",
    )
    val username: String,
    @field:Min(1000)
    @field:Max(3999)
    val studentNumber: Int,
    @field:Pattern(
        regexp = "^[가-힣]+$",
    )
    val name: String,
    @field:Email
    val email: String,
    @field:Size(min = 6, max = 6)
    @field:NotBlank
    val otp: String,
)
