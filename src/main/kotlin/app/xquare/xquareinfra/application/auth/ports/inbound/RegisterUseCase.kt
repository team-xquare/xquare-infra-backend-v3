package app.xquare.xquareinfra.application.auth.ports.inbound

data class RegisterCommand(
    val username: String,
    val password: String,
    val studentNumber: Int,
    val name: String,
    val email: String,
)

sealed class RegisterResult {
    data class Success(
        val accessToken: String,
        val refreshToken: String,
    ) : RegisterResult()

    data object UsernameAlreadyExists : RegisterResult()
}

interface RegisterUseCase {
    fun register(command: RegisterCommand): RegisterResult
}
