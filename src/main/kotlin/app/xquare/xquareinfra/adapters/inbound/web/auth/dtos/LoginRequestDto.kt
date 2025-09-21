package app.xquare.xquareinfra.adapters.inbound.web.auth.dtos

data class LoginRequestDto(
    val username: String,
    val password: String,
)
