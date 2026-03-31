package app.xquare.xquareinfra.adapters.inbound.web.auth.dtos

import jakarta.validation.constraints.*

data class VerifyOtpRequestDto (
    @field:NotBlank @field:Email val email: String,
    @field:Size(min= 6, max =6) @field:NotBlank val otp: String,
)