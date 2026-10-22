package app.xquare.xquareinfra.application.emailOtp

import app.xquare.xquareinfra.application.auth.AuthException
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailOtpPort
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailSendPort
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.OtpConsumeResult
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.Year
import java.util.UUID

@Service
class EmailOtpService(
    private val emailOtpPort: EmailOtpPort,
    private val emailSendPort: EmailSendPort,
    private val emailOtpProperties: EmailOtpProperties,
) {
    companion object {
        private const val MAX_OTP_FAILURES = 5
    }

    private val secureRandom = SecureRandom()

    fun sendOtp(
        email: String,
        purpose: EmailOtpPurpose,
        recipientEmail: String? = email,
    ) {
        if (recipientEmail == null) {
            return
        }

        val permitted =
            emailOtpPort.tryAcquireSendPermit(
                purpose = purpose,
                email = email,
                maxRequests = emailOtpProperties.sendRateLimitMaxRequests,
                windowSeconds = emailOtpProperties.sendRateLimitWindowSeconds,
            )
        if (!permitted) {
            throw AuthException.OtpRateLimitExceeded
        }

        val otp = generateCode()
        emailOtpPort.saveOtp(
            purpose = purpose,
            email = recipientEmail,
            otp = otp,
            ttlSeconds = emailOtpProperties.otpTtlSeconds,
        )

        emailSendPort.sendWithTemplate(
            to = recipientEmail,
            subject = getSubject(purpose),
            templateName = emailOtpProperties.templateName,
            variables =
                mapOf(
                    "otp" to otp,
                    "otpGuideText" to getGuideText(purpose),
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
        when (emailOtpPort.consumeOtp(purpose, email, otp, MAX_OTP_FAILURES)) {
            OtpConsumeResult.CONSUMED -> return
            OtpConsumeResult.NOT_FOUND -> throw AuthException.OtpNotFound
            OtpConsumeResult.MISMATCH -> throw AuthException.OtpMismatch
        }
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

    fun consumeVerifiedEmail(
        token: String,
        purpose: EmailOtpPurpose,
        expectedEmail: String? = null,
    ): String? = emailOtpPort.consumeVerifiedToken(purpose, token, expectedEmail)

    private fun getSubject(purpose: EmailOtpPurpose): String =
        when (purpose) {
            EmailOtpPurpose.REGISTER -> emailOtpProperties.registerSubject
            EmailOtpPurpose.USERNAME_RECOVERY -> emailOtpProperties.usernameRecoverySubject
            EmailOtpPurpose.PASSWORD_RESET -> emailOtpProperties.passwordResetSubject
        }

    private fun getGuideText(purpose: EmailOtpPurpose): String =
        when (purpose) {
            EmailOtpPurpose.REGISTER -> "회원가입을 완료하려면 아래 인증 코드를 입력해주세요."
            EmailOtpPurpose.USERNAME_RECOVERY -> "아이디를 확인하려면 아래 인증 코드를 입력해주세요."
            EmailOtpPurpose.PASSWORD_RESET -> "비밀번호를 재설정하려면 아래 인증 코드를 입력해주세요."
        }

    private fun generateCode(): String = (secureRandom.nextInt(900000) + 100000).toString()

    private fun generateVerifiedToken(): String = UUID.randomUUID().toString()
}
