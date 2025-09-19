package app.xquare.xquareinfra.adapters.outbound.token

import app.xquare.xquareinfra.application.auth.ports.outbound.AccessTokenPort
import app.xquare.xquareinfra.infrastructure.jwt.JwtProvider
import org.springframework.stereotype.Component

@Component
class JwtAccessTokenAdapter(
    private val jwtProvider: JwtProvider,
) : AccessTokenPort {
    override fun create(userId: Long): String = jwtProvider.createAccessToken(userId)
}
