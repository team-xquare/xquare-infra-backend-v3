package app.xquare.xquareinfra.adapters.inbound.web.auth

data class LoginRequestDto(
    val username: String,
    val password: String,
)
