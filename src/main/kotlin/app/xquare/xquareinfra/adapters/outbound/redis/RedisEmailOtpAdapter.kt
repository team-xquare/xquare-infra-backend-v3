package app.xquare.xquareinfra.adapters.outbound.redis

import app.xquare.xquareinfra.application.auth.ports.outbound.EmailOtpPort as AuthEmailOtpPort
import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailOtpPort as SharedEmailOtpPort
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.OtpConsumeResult
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.concurrent.TimeUnit

@Component
class RedisEmailOtpAdapter(
    private val redisTemplate: StringRedisTemplate,
) : AuthEmailOtpPort,
    SharedEmailOtpPort {
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
                        local failures = redis.call('INCR', KEYS[2])
                        local ttl = redis.call('TTL', KEYS[1])
                        if ttl > 0 then
                            redis.call('EXPIRE', KEYS[2], ttl)
                        end
                        if failures >= tonumber(ARGV[2]) then
                            redis.call('DEL', KEYS[1])
                            redis.call('DEL', KEYS[2])
                        end
                        return 0
                    end
                    redis.call('DEL', KEYS[1])
                    redis.call('DEL', KEYS[2])
                    return 1
                    """.trimIndent(),
                )
                resultType = Long::class.java
            }
        private val CONSUME_VERIFIED_TOKEN_SCRIPT =
            DefaultRedisScript<String>().apply {
                setScriptText(
                    """
                    local current = redis.call('GET', KEYS[1])
                    if not current then
                        return nil
                    end
                    if ARGV[1] ~= '' and current ~= ARGV[1] then
                        return nil
                    end
                    redis.call('DEL', KEYS[1])
                    return current
                    """.trimIndent(),
                )
                resultType = String::class.java
            }
    }

    private fun otpKey(
        purpose: EmailOtpPurpose,
        email: String,
    ): String = "otp:${purpose.key}:${hashEmail(email)}"

    private fun legacyOtpKey(email: String): String = "otp$email"

    private fun verifiedTokenKey(
        purpose: EmailOtpPurpose,
        token: String,
    ): String = "verified:${purpose.key}:$token"

    private fun legacyVerifiedTokenKey(token: String): String = "verifiedToken$token"

    private fun otpFailureKey(
        purpose: EmailOtpPurpose,
        email: String,
    ): String = "${otpKey(purpose, email)}:failures"

    private fun hashEmail(email: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(email.trim().lowercase().toByteArray())
            .joinToString("") { "%02x".format(it) }

    private fun normalizeEmail(email: String): String = email.trim().lowercase()

    override fun saveOtp(
        email: String,
        otp: String,
        ttlSeconds: Long,
    ) {
        redisTemplate.opsForValue().set(legacyOtpKey(email), otp, ttlSeconds, TimeUnit.SECONDS)
    }

    override fun getOtp(email: String): String? = redisTemplate.opsForValue().get(legacyOtpKey(email))

    override fun deleteOtp(email: String) {
        redisTemplate.delete(legacyOtpKey(email))
    }

    override fun saveVerifiedToken(
        token: String,
        email: String,
        ttlSeconds: Long,
    ) {
        redisTemplate.opsForValue().set(legacyVerifiedTokenKey(token), email, ttlSeconds, TimeUnit.SECONDS)
    }

    override fun getEmailByVerifiedToken(token: String): String? =
        redisTemplate.opsForValue().get(legacyVerifiedTokenKey(token))

    override fun deleteVerifiedToken(token: String) {
        redisTemplate.delete(legacyVerifiedTokenKey(token))
    }

    override fun saveOtp(
        purpose: EmailOtpPurpose,
        email: String,
        otp: String,
        ttlSeconds: Long,
    ) {
        redisTemplate.opsForValue().set(otpKey(purpose, email), otp, ttlSeconds, TimeUnit.SECONDS)
        redisTemplate.delete(otpFailureKey(purpose, email))
    }

    override fun getOtp(
        purpose: EmailOtpPurpose,
        email: String,
    ): String? = redisTemplate.opsForValue().get(otpKey(purpose, email))

    override fun consumeOtp(
        purpose: EmailOtpPurpose,
        email: String,
        otp: String,
        maxFailures: Int,
    ): OtpConsumeResult {
        val result =
            redisTemplate.execute(
                CONSUME_OTP_SCRIPT,
                listOf(otpKey(purpose, email), otpFailureKey(purpose, email)),
                otp,
                maxFailures.toString(),
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
        val storedEmail =
            when (purpose) {
                EmailOtpPurpose.REGISTER -> normalizeEmail(email)
                else -> email
            }
        redisTemplate.opsForValue().set(verifiedTokenKey(purpose, token), storedEmail, ttlSeconds, TimeUnit.SECONDS)
    }

    override fun consumeVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
        expectedEmail: String?,
    ): String? =
        redisTemplate.execute(
            CONSUME_VERIFIED_TOKEN_SCRIPT,
            listOf(verifiedTokenKey(purpose, token)),
            when (purpose) {
                EmailOtpPurpose.REGISTER -> expectedEmail?.let(::normalizeEmail)
                else -> expectedEmail
            }.orEmpty(),
        )
}
