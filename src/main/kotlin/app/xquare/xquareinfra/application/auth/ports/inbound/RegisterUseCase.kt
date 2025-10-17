package app.xquare.xquareinfra.application.auth.ports.inbound

data class RegisterCommand(
    val username: String,
    val password: String,
    val studentNumber: Int,
    val name: String,
    val email: String,
)

data class RegisterResult(
    val accessToken: String,
    val refreshToken: String,
)

interface RegisterUseCase {
    fun register(command: RegisterCommand): RegisterResult
}
