package app.xquare.xquareinfra.application.auth.ports.inbound

data class RefreshTokenCommand(
    val refreshToken: String,
)

data class RefreshTokenResult(
    val accessToken: String,
    val refreshToken: String,
)

interface RefreshTokenUseCase {
    fun refreshToken(command: RefreshTokenCommand): RefreshTokenResult
}
