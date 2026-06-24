package app.xquare.xquareinfra.adapters.outbound.redis

import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.OtpConsumeResult
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import app.xquare.xquareinfra.application.auth.ports.outbound.EmailOtpPort as AuthEmailOtpPort
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailOtpPort as SharedEmailOtpPort

@Component
class RedisEmailOtpAdapter(
    private val redisTemplate: StringRedisTemplate,
) : AuthEmailOtpPort,
    SharedEmailOtpPort {
    companion object {
        private val ACQUIRE_OTP_SEND_PERMIT_SCRIPT =
            RedisLuaScriptLoader.load("redis/scripts/acquire-otp-send-permit.lua", Long::class.java)
        private val CONSUME_OTP_SCRIPT =
            RedisLuaScriptLoader.load("redis/scripts/consume-otp.lua", Long::class.java)
        private val CONSUME_VERIFIED_TOKEN_SCRIPT =
            RedisLuaScriptLoader.load("redis/scripts/consume-verified-token.lua", String::class.java)
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

    private fun otpSendRateLimitKey(
        purpose: EmailOtpPurpose,
        email: String,
    ): String = "otp-rate-limit:${purpose.key}:${hashEmail(email)}"

    private fun hashEmail(email: String): String =
        MessageDigest
            .getInstance("SHA-256")
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

    override fun getEmailByVerifiedToken(token: String): String? = redisTemplate.opsForValue().get(legacyVerifiedTokenKey(token))

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

    override fun tryAcquireSendPermit(
        purpose: EmailOtpPurpose,
        email: String,
        maxRequests: Int,
        windowSeconds: Long,
    ): Boolean =
        redisTemplate.execute(
            ACQUIRE_OTP_SEND_PERMIT_SCRIPT,
            listOf(otpSendRateLimitKey(purpose, email)),
            windowSeconds.toString(),
            maxRequests.toString(),
        ) == 1L

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
        redisTemplate.opsForValue().set(
            verifiedTokenKey(purpose, token),
            normalizeEmail(email),
            ttlSeconds,
            TimeUnit.SECONDS,
        )
    }

    override fun consumeVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
        expectedEmail: String?,
    ): String? =
        redisTemplate.execute(
            CONSUME_VERIFIED_TOKEN_SCRIPT,
            listOf(verifiedTokenKey(purpose, token)),
            expectedEmail?.let(::normalizeEmail).orEmpty(),
        )
}
