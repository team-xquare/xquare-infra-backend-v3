package app.xquare.xquareinfra.adapters.outbound.token

import app.xquare.xquareinfra.application.auth.ports.outbound.RefreshTokenPort
import app.xquare.xquareinfra.infrastructure.jwt.JwtProvider
import org.springframework.stereotype.Component

@Component
class JwtRefreshTokenAdapter(
    private val jwtProvider: JwtProvider,
) : RefreshTokenPort {
    override fun isValid(refreshToken: String): Boolean = jwtProvider.isValid(refreshToken)

    override fun extractUserId(refreshToken: String): Long? = jwtProvider.extractUserId(refreshToken)

    override fun create(userId: Long): String = jwtProvider.createRefreshToken(userId)
}
