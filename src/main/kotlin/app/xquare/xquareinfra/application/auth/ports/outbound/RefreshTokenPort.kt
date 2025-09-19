package app.xquare.xquareinfra.application.auth.ports.outbound

interface RefreshTokenPort {
    fun create(userId: Long): String

    fun isValid(refreshToken: String): Boolean

    fun extractUserId(refreshToken: String): Long?
}
