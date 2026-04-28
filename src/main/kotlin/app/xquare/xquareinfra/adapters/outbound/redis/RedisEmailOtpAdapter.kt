package app.xquare.xquareinfra.adapters.outbound.redis

import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailOtpPort
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RedisEmailOtpAdapter(
    private val redisTemplate: StringRedisTemplate,
) : EmailOtpPort {
    private fun otpKey(
        purpose: EmailOtpPurpose,
        email: String,
    ): String = "otp:${purpose.key}:$email"

    private fun verifiedTokenKey(
        purpose: EmailOtpPurpose,
        token: String,
    ): String = "verified:${purpose.key}:$token"

    override fun saveOtp(
        purpose: EmailOtpPurpose,
        email: String,
        otp: String,
        ttlSeconds: Long,
    ) {
        redisTemplate.opsForValue().set(otpKey(purpose, email), otp, ttlSeconds, TimeUnit.SECONDS)
    }

    override fun getOtp(
        purpose: EmailOtpPurpose,
        email: String,
    ): String? = redisTemplate.opsForValue().get(otpKey(purpose, email))

    override fun deleteOtp(
        purpose: EmailOtpPurpose,
        email: String,
    ) {
        redisTemplate.delete(otpKey(purpose, email))
    }

    override fun saveVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
        email: String,
        ttlSeconds: Long,
    ) {
        redisTemplate.opsForValue().set(verifiedTokenKey(purpose, token), email, ttlSeconds, TimeUnit.SECONDS)
    }

    override fun getEmailByVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
    ): String? = redisTemplate.opsForValue().get(verifiedTokenKey(purpose, token))

    override fun deleteVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
    ) {
        redisTemplate.delete(verifiedTokenKey(purpose, token))
    }
}
