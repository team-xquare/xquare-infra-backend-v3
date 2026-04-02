package app.xquare.xquareinfra.adapters.outbound.redis

import app.xquare.xquareinfra.application.auth.ports.outbound.EmailOtpPort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisEmailOtpAdapter(
    private val redisTemplate: StringRedisTemplate,
) : EmailOtpPort {
    override fun saveOtp(
        email: String,
        otp: String,
        ttlSeconds: Long,
    ) {
        redisTemplate.opsForValue().set("otp$email", otp, ttlSeconds, TimeUnit.SECONDS)
    }

    override fun getOtp(email: String): String? = redisTemplate.opsForValue().get("otp$email")

    override fun deleteOtp(email: String) {
        redisTemplate.delete("otp$email")
    }

    override fun saveVerifiedToken(
        token: String,
        email: String,
        ttlSeconds: Long,
    ) {
        redisTemplate.opsForValue().set("verifiedToken$token", email, ttlSeconds, TimeUnit.SECONDS)
    }

    override fun getEmailByVerifiedToken(token: String): String? = redisTemplate.opsForValue().get("verified$token")

    override fun deleteVerifiedToken(token: String) {
        redisTemplate.delete("verified$token")
    }
}
