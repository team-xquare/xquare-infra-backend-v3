package app.xquare.xquareinfra.application.emailOtp

import app.xquare.xquareinfra.application.auth.AuthException
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailOtpPort
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailSendPort
import org.springframework.stereotype.Service
import java.time.Year
import java.security.SecureRandom
import java.util.UUID

@Service
class EmailOtpService(
    private val emailOtpPort: EmailOtpPort,
    private val emailSendPort: EmailSendPort,
    private val emailOtpProperties: EmailOtpProperties,
) {
    private val secureRandom = SecureRandom()

    fun sendOtp(
        email: String,
        purpose: EmailOtpPurpose,
    ) {
        val otp = generateCode()
        emailOtpPort.saveOtp(
            purpose = purpose,
            email = email,
            otp = otp,
            ttlSeconds = emailOtpProperties.otpTtlSeconds,
        )

        emailSendPort.sendWithTemplate(
            to = email,
            subject = getSubject(purpose),
            templateName = emailOtpProperties.templateName,
            variables = mapOf(
                "otp" to otp,
                "expiresIn" to emailOtpProperties.expiresInText,
                "supportEmail" to emailOtpProperties.supportEmail,
                "year" to Year.now().value,
            ),
        )
    }

    fun verifyOtp(
        email: String,
        otp: String,
        purpose: EmailOtpPurpose,
    ) {
        val savedOtp = emailOtpPort.getOtp(purpose, email)
            ?: throw AuthException.OtpNotFound

        if (savedOtp != otp) {
            throw AuthException.OtpMismatch
        }

        emailOtpPort.deleteOtp(purpose, email)
    }

    fun verifyOtpAndIssueVerifiedToken(
        email: String,
        otp: String,
        purpose: EmailOtpPurpose,
    ): String {
        verifyOtp(email, otp, purpose)

        val verifiedToken = generateVerifiedToken()
        emailOtpPort.saveVerifiedToken(
            purpose = purpose,
            token = verifiedToken,
            email = email,
            ttlSeconds = emailOtpProperties.verifiedTokenTtlSeconds,
        )

        return verifiedToken
    }

    fun getVerifiedEmail(
        token: String,
        purpose: EmailOtpPurpose,
    ): String? = emailOtpPort.getEmailByVerifiedToken(purpose, token)

    fun deleteVerifiedToken(
        token: String,
        purpose: EmailOtpPurpose,
    ) {
        emailOtpPort.deleteVerifiedToken(purpose, token)
    }

    private fun getSubject(purpose: EmailOtpPurpose): String =
        when (purpose) {
            EmailOtpPurpose.REGISTER -> emailOtpProperties.registerSubject
            EmailOtpPurpose.USERNAME_RECOVERY -> emailOtpProperties.usernameRecoverySubject
        }

    private fun generateCode(): String = (secureRandom.nextInt(900000) + 100000).toString()

    private fun generateVerifiedToken(): String = UUID.randomUUID().toString()
}
