package app.xquare.xquareinfra.application.emailOtp

import app.xquare.xquareinfra.application.auth.AuthException
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailOtpPort
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailSendPort
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.OtpConsumeResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.assertThrows

class EmailOtpServiceTest {
    @Test
    fun `different purposes do not share otp storage`() {
        val fixture = createFixture()

        fixture.emailOtpService.sendOtp("user@test.com", EmailOtpPurpose.REGISTER)
        fixture.emailOtpService.sendOtp("user@test.com", EmailOtpPurpose.USERNAME_RECOVERY)

        assertNotNull(fixture.emailOtpPort.getOtp(EmailOtpPurpose.REGISTER, "user@test.com"))
        assertNotNull(fixture.emailOtpPort.getOtp(EmailOtpPurpose.USERNAME_RECOVERY, "user@test.com"))
        assertEquals("[Xquare] 이메일 인증 코드", fixture.emailSendPort.sentEmails[0].subject)
        assertEquals("[Xquare] 아이디 찾기 코드", fixture.emailSendPort.sentEmails[1].subject)
    }

    @Test
    fun `verifying otp with wrong purpose fails`() {
        val fixture = createFixture()
        fixture.emailOtpPort.saveOtp(
            purpose = EmailOtpPurpose.REGISTER,
            email = "user@test.com",
            otp = "123456",
            ttlSeconds = 300,
        )

        assertThrows<AuthException.OtpNotFound> {
            fixture.emailOtpService.verifyOtp(
                email = "user@test.com",
                otp = "123456",
                purpose = EmailOtpPurpose.USERNAME_RECOVERY,
            )
        }
    }

    @Test
    fun `verifying otp locks it after repeated mismatches`() {
        val fixture = createFixture()
        fixture.emailOtpPort.saveOtp(
            purpose = EmailOtpPurpose.REGISTER,
            email = "user@test.com",
            otp = "123456",
            ttlSeconds = 300,
        )

        repeat(5) {
            assertThrows<AuthException.OtpMismatch> {
                fixture.emailOtpService.verifyOtp(
                    email = "user@test.com",
                    otp = "000000",
                    purpose = EmailOtpPurpose.REGISTER,
                )
            }
        }

        assertThrows<AuthException.OtpNotFound> {
            fixture.emailOtpService.verifyOtp(
                email = "user@test.com",
                otp = "123456",
                purpose = EmailOtpPurpose.REGISTER,
            )
        }
    }

    private fun createFixture(): Fixture {
        val emailOtpPort = FakeEmailOtpPort()
        val emailSendPort = FakeEmailSendPort()
        val emailOtpService =
            EmailOtpService(
                emailOtpPort = emailOtpPort,
                emailSendPort = emailSendPort,
                emailOtpProperties =
                    EmailOtpProperties(
                        supportEmail = "support@test.com",
                        templateName = "email/otp",
                        expiresInText = "5분",
                        otpTtlSeconds = 300,
                        verifiedTokenTtlSeconds = 600,
                        registerSubject = "[Xquare] 이메일 인증 코드",
                        usernameRecoverySubject = "[Xquare] 아이디 찾기 코드",
                        passwordResetSubject = "[Xquare] 비밀번호 재설정 코드",
                    ),
            )

        return Fixture(emailOtpService, emailOtpPort, emailSendPort)
    }

    private data class Fixture(
        val emailOtpService: EmailOtpService,
        val emailOtpPort: FakeEmailOtpPort,
        val emailSendPort: FakeEmailSendPort,
    )

    private class FakeEmailSendPort : EmailSendPort {
        val sentEmails = mutableListOf<SentEmail>()

        override fun send(
            to: String,
            subject: String,
            body: String,
        ) {
            sentEmails += SentEmail(to = to, subject = subject)
        }

        override fun sendWithTemplate(
            to: String,
            subject: String,
            templateName: String,
            variables: Map<String, Any>,
        ) {
            sentEmails += SentEmail(to = to, subject = subject)
        }
    }

    private class FakeEmailOtpPort : EmailOtpPort {
        private val otps = mutableMapOf<Pair<EmailOtpPurpose, String>, String>()
        private val otpFailures = mutableMapOf<Pair<EmailOtpPurpose, String>, Int>()
        private val verifiedTokens = mutableMapOf<Pair<EmailOtpPurpose, String>, String>()

        override fun saveOtp(
            purpose: EmailOtpPurpose,
            email: String,
            otp: String,
            ttlSeconds: Long,
        ) {
            otps[purpose to email] = otp
            otpFailures.remove(purpose to email)
        }

        override fun getOtp(
            purpose: EmailOtpPurpose,
            email: String,
        ): String? = otps[purpose to email]

        override fun consumeOtp(
            purpose: EmailOtpPurpose,
            email: String,
            otp: String,
        ): OtpConsumeResult {
            val key = purpose to email
            val savedOtp = otps[key] ?: return OtpConsumeResult.NOT_FOUND
            if (savedOtp != otp) {
                return OtpConsumeResult.MISMATCH
            }
            otps.remove(key)
            otpFailures.remove(key)
            return OtpConsumeResult.CONSUMED
        }

        override fun recordOtpFailure(
            purpose: EmailOtpPurpose,
            email: String,
            ttlSeconds: Long,
            maxFailures: Int,
        ) {
            val key = purpose to email
            val failures = (otpFailures[key] ?: 0) + 1
            if (failures >= maxFailures) {
                otps.remove(key)
                otpFailures.remove(key)
                return
            }
            otpFailures[key] = failures
        }

        override fun saveVerifiedToken(
            purpose: EmailOtpPurpose,
            token: String,
            email: String,
            ttlSeconds: Long,
        ) {
            verifiedTokens[purpose to token] = email
        }

        override fun consumeVerifiedToken(
            purpose: EmailOtpPurpose,
            token: String,
            expectedEmail: String?,
        ): String? {
            val key = purpose to token
            val email = verifiedTokens[key] ?: return null
            if (expectedEmail != null && email != expectedEmail) {
                return null
            }
            verifiedTokens.remove(key)
            return email
        }
    }

    private data class SentEmail(
        val to: String,
        val subject: String,
    )
}
