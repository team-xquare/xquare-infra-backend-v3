package app.xquare.xquareinfra.adapters.inbound.web.auth.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SendOtpRequestDto(
    @field:Email @field:NotBlank val email: String,
)
