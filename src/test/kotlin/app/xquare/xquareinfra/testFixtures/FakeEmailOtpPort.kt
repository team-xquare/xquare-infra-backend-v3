package app.xquare.xquareinfra.testFixtures

import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailOtpPort
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.OtpConsumeResult

class FakeEmailOtpPort : EmailOtpPort {
    private val otps = mutableMapOf<Pair<EmailOtpPurpose, String>, String>()
    private val otpFailures = mutableMapOf<Pair<EmailOtpPurpose, String>, Int>()
    private val verifiedTokens = mutableMapOf<Pair<EmailOtpPurpose, String>, String>()
    private val sendRequests = mutableMapOf<Pair<EmailOtpPurpose, String>, Int>()

    override fun tryAcquireSendPermit(
        purpose: EmailOtpPurpose,
        email: String,
        maxRequests: Int,
        windowSeconds: Long,
    ): Boolean {
        val key = purpose to normalizeEmail(email)
        val requests = (sendRequests[key] ?: 0) + 1
        sendRequests[key] = requests
        return requests <= maxRequests
    }

    override fun saveOtp(
        purpose: EmailOtpPurpose,
        email: String,
        otp: String,
        ttlSeconds: Long,
    ) {
        val key = purpose to normalizeEmail(email)
        otps[key] = otp
        otpFailures.remove(key)
    }

    override fun getOtp(
        purpose: EmailOtpPurpose,
        email: String,
    ): String? = otps[purpose to normalizeEmail(email)]

    override fun consumeOtp(
        purpose: EmailOtpPurpose,
        email: String,
        otp: String,
        maxFailures: Int,
    ): OtpConsumeResult {
        val key = purpose to normalizeEmail(email)
        val savedOtp = otps[key] ?: return OtpConsumeResult.NOT_FOUND
        if (savedOtp != otp) {
            val failures = (otpFailures[key] ?: 0) + 1
            if (failures >= maxFailures) {
                otps.remove(key)
                otpFailures.remove(key)
            } else {
                otpFailures[key] = failures
            }
            return OtpConsumeResult.MISMATCH
        }

        otps.remove(key)
        otpFailures.remove(key)
        return OtpConsumeResult.CONSUMED
    }

    override fun saveVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
        email: String,
        ttlSeconds: Long,
    ) {
        verifiedTokens[purpose to token] = normalizeEmail(email)
    }

    override fun consumeVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
        expectedEmail: String?,
    ): String? {
        val key = purpose to token
        val email = verifiedTokens[key] ?: return null
        if (expectedEmail != null && email != normalizeEmail(expectedEmail)) {
            return null
        }
        verifiedTokens.remove(key)
        return email
    }

    fun hasOtp(
        purpose: EmailOtpPurpose,
        email: String,
    ): Boolean = otps.containsKey(purpose to normalizeEmail(email))

    fun hasVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
    ): Boolean = verifiedTokens.containsKey(purpose to token)

    private fun normalizeEmail(email: String): String = email.trim().lowercase()
}
