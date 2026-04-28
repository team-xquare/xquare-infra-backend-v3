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

    fun deleteOtp(
        purpose: EmailOtpPurpose,
        email: String,
    )

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
