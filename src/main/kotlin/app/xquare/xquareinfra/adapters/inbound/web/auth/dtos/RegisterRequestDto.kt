package app.xquare.xquareinfra.adapters.inbound.web.auth.dtos

import jakarta.validation.constraints.*

data class RegisterRequestDto(
    @field:Size(min = 4, max = 20)
    @field:Pattern(
        regexp = "^[A-z0-9_-]+$",
    )
    val username: String,
    @field:Size(min = 8, max = 20)
    @field:Pattern(
        regexp = ".*[!@#\$%^&*(),.?\":{}|<>].*",
    )
    val password: String,
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
