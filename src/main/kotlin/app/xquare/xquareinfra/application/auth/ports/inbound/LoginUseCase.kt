package app.xquare.xquareinfra.application.auth.ports.inbound

data class LoginCommand(
    val username: String,
    val password: String,
)

sealed class LoginResult {
    data class Success(
        val accessToken: String,
        val refreshToken: String,
    ) : LoginResult()

    data object InvalidCredentials : LoginResult()
}

interface LoginUseCase {
    fun login(command: LoginCommand): LoginResult
}
