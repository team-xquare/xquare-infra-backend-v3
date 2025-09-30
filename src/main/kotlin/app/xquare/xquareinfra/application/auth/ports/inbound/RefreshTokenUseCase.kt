package app.xquare.xquareinfra.application.auth.ports.inbound

data class RefreshTokenCommand(
    val refreshToken: String,
)

sealed class RefreshTokenResult {
    data class Success(
        val accessToken: String,
        val refreshToken: String,
    ) : RefreshTokenResult()

    data object InvalidRefreshToken : RefreshTokenResult()
}

interface RefreshTokenUseCase {
    fun refreshToken(command: RefreshTokenCommand): RefreshTokenResult
}
