package app.xquare.xquareinfra.adapters.inbound.web.auth

data class RegisterRequestDto(
    val username: String,
    val password: String,
    val studentNumber: Int,
    val name: String,
    val email: String,
)
