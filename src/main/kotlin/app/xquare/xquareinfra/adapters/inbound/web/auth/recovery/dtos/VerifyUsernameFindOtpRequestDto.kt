package app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class VerifyUsernameFindOtpRequestDto(
    @field:Min(1000)
    @field:Max(3999)
    val studentNumber: Int,
    @field:Pattern(
        regexp = "^[A-Za-z가-힣]+(?:[ -][A-Za-z가-힣]+)*$",
    )
    @field:NotBlank
    val name: String,
    @field:Email
    @field:NotBlank
    val email: String,
    @field:Pattern(regexp = "^\\d{6}$")
    @field:Size(min = 6, max = 6)
    @field:NotBlank
    val otp: String,
)
