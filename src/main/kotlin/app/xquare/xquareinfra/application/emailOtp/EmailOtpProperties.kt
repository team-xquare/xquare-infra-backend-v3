package app.xquare.xquareinfra.application.emailOtp

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "email-otp")
data class EmailOtpProperties(
    val supportEmail: String,
    val templateName: String,
    val expiresInText: String,
    val otpTtlSeconds: Long,
    val verifiedTokenTtlSeconds: Long,
    val registerSubject: String,
    val usernameRecoverySubject: String,
    val passwordResetSubject: String,
)
