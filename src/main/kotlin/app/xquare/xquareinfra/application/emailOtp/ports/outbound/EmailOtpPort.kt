package app.xquare.xquareinfra.application.emailOtp.ports.outbound

import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose

interface EmailOtpPort {
    fun tryAcquireSendPermit(
        purpose: EmailOtpPurpose,
        email: String,
        maxRequests: Int,
        windowSeconds: Long,
    ): Boolean

    fun saveOtp(
        purpose: EmailOtpPurpose,
        email: String,
        otp: String,
        ttlSeconds: Long,
    )

    fun getOtp(
        purpose: EmailOtpPurpose,
        email: String,
    ): String?

    fun consumeOtp(
        purpose: EmailOtpPurpose,
        email: String,
        otp: String,
        maxFailures: Int,
    ): OtpConsumeResult

    fun saveVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
        email: String,
        ttlSeconds: Long,
    )

    fun consumeVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
        expectedEmail: String? = null,
    ): String?
}

enum class OtpConsumeResult {
    CONSUMED,
    NOT_FOUND,
    MISMATCH,
}
