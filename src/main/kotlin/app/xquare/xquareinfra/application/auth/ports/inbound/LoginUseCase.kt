package app.xquare.xquareinfra.application.auth.ports.inbound

data class LoginCommand(
    val username: String,
    val password: String,
)

data class LoginResult(
    val accessToken: String,
    val refreshToken: String,
)

interface LoginUseCase {
    fun login(command: LoginCommand): LoginResult
}
