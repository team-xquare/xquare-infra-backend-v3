package app.xquare.xquareinfra.adapters.inbound.web.auth.dtos

import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class LoginRequestDto(
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
)
