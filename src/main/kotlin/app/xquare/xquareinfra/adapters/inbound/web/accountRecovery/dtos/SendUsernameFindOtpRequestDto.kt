package app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern

data class SendUsernameFindOtpRequestDto(
    @field:Min(1000)
    @field:Max(3999)
    val studentNumber: Int,
    @field:Pattern(
        regexp = "^[가-힣]+$",
    )
    val name: String,
    @field:Email
    val email: String,
)
