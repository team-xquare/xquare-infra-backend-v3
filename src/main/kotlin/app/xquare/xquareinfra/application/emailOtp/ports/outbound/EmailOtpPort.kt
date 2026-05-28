package app.xquare.xquareinfra.application.emailOtp.ports.outbound

import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose

interface EmailOtpPort {
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
    ): OtpConsumeResult

    fun saveVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
        email: String,
        ttlSeconds: Long,
    )

    fun getEmailByVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
    ): String?

    fun deleteVerifiedToken(
        purpose: EmailOtpPurpose,
        token: String,
    )
}

enum class OtpConsumeResult {
    CONSUMED,
    NOT_FOUND,
    MISMATCH,
}
