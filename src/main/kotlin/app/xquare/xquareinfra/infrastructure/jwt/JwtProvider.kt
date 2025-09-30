package app.xquare.xquareinfra.infrastructure.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties,
) {
    private val key = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun createAccessToken(userId: Long): String {
        val now = Date()
        val accessTokenExpiry = Date(now.time + jwtProperties.accessTokenExpiration)

        return Jwts
            .builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(accessTokenExpiry)
            .signWith(key)
            .compact()
    }

    fun createRefreshToken(userId: Long): String {
        val now = Date()
        val refreshTokenExpiry = Date(now.time + jwtProperties.refreshTokenExpiration)

        return Jwts
            .builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(refreshTokenExpiry)
            .signWith(key)
            .compact()
    }

    fun isValid(token: String): Boolean =
        try {
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }

    fun extractUserId(token: String): Long? =
        try {
            Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload.subject
                .toLong()
        } catch (e: Exception) {
            null
        }
}
