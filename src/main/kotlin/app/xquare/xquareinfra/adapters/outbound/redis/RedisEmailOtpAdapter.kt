package app.xquare.xquareinfra.adapters.outbound.redis

import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailOtpPort
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.OtpConsumeResult
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

@Component
class RedisEmailOtpAdapter(
    private val redisTemplate: StringRedisTemplate,
) : EmailOtpPort {
    companion object {
        private val CONSUME_OTP_SCRIPT =
            DefaultRedisScript<Long>().apply {
                setScriptText(
                    """
                    local current = redis.call('GET', KEYS[1])
                    if not current then
                        return -1
                    end
                    if current ~= ARGV[1] then
                        return 0
                    end
                    redis.call('DEL', KEYS[1])
                    return 1
                    """.trimIndent(),
                )
                resultType = Long::class.java
            }
    }

    private fun otpKey(
        purpose: EmailOtpPurpose,
        email: String,
    ): String = "otp:${purpose.key}:${hashEmail(email)}"

    private fun verifiedTokenKey(
        purpose: EmailOtpPurpose,
        token: String,
    ): String = "verified:${purpose.key}:$token"

    private fun hashEmail(email: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(email.trim().lowercase().toByteArray())
            .joinToString("") { "%02x".format(it) }

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

    override fun consumeOtp(
        purpose: EmailOtpPurpose,
        email: String,
        otp: String,
    ): OtpConsumeResult {
        val result =
            redisTemplate.execute(
                CONSUME_OTP_SCRIPT,
                listOf(otpKey(purpose, email)),
                otp,
            )

        return when (result) {
            1L -> OtpConsumeResult.CONSUMED
            0L -> OtpConsumeResult.MISMATCH
            else -> OtpConsumeResult.NOT_FOUND
        }
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
