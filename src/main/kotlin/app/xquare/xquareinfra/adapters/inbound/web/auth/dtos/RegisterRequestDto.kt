package app.xquare.xquareinfra.adapters.inbound.web.auth.dtos

data class RegisterRequestDto(
    val username: String,
    val password: String,
    val studentNumber: Int,
    val name: String,
    val email: String,
)
