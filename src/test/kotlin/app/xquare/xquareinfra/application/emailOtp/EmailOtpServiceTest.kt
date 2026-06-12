package app.xquare.xquareinfra.application.emailOtp

import app.xquare.xquareinfra.application.auth.AuthException
import app.xquare.xquareinfra.testFixtures.FakeEmailOtpPort
import app.xquare.xquareinfra.testFixtures.FakeEmailSendPort
import java.time.Year
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
    fun `sendOtp passes purpose specific template variables`() {
        val fixture = createFixture()

        fixture.emailOtpService.sendOtp("user@test.com", EmailOtpPurpose.PASSWORD_RESET)

        val sentEmail = fixture.emailSendPort.sentEmails.single()
        assertEquals("email/otp", sentEmail.templateName)
        assertEquals("비밀번호를 재설정하려면 아래 인증 코드를 입력해주세요.", sentEmail.variables["otpGuideText"])
        assertEquals("5분", sentEmail.variables["expiresIn"])
        assertEquals("support@test.com", sentEmail.variables["supportEmail"])
        assertEquals(Year.now().value, sentEmail.variables["year"])
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
}
