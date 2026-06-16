package app.xquare.xquareinfra.adapters.inbound.web.auth.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class VerifyOtpRequestDto(
    @field:NotBlank @field:Email val email: String,
    @field:Pattern(regexp = "^\\d{6}$")
    @field:Size(min = 6, max = 6) @field:NotBlank val otp: String,
)
