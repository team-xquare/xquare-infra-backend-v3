package app.xquare.xquareinfra.application.accountRecovery

import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.ResetPasswordCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendPasswordResetOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendUsernameFindOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyPasswordResetOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyUsernameFindOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.outbound.UserPersistenceForAccountRecoveryPort
import app.xquare.xquareinfra.application.auth.AuthException
import app.xquare.xquareinfra.application.auth.ports.outbound.PasswordEncoderPort
import app.xquare.xquareinfra.application.emailOtp.EmailOtpProperties
import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import app.xquare.xquareinfra.application.emailOtp.EmailOtpService
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailOtpPort
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailSendPort
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.domain.user.UserRole
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import org.junit.jupiter.api.assertThrows

class AccountRecoveryServiceTest {
    @Test
    fun `sendUsernameFindOtp sends otp when studentNumber name email all match`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())

        fixture.accountRecoveryService.sendUsernameFindOtp(
            SendUsernameFindOtpCommand(
                studentNumber = 1101,
                name = "테스터",
                email = "user@test.com",
            ),
        )

        val sentEmail = fixture.emailSendPort.sentEmails.single()
        assertEquals("[Xquare] 아이디 찾기 코드", sentEmail.subject)
        assertNotNull(fixture.emailOtpPort.getOtp(EmailOtpPurpose.USERNAME_RECOVERY, "user@test.com"))
    }

    @Test
    fun `sendUsernameFindOtp throws generic mismatch when any identity field is wrong`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())

        assertThrows<AuthException.InvalidUserInfo> {
            fixture.accountRecoveryService.sendUsernameFindOtp(
                SendUsernameFindOtpCommand(
                    studentNumber = 1101,
                    name = "다른이름",
                    email = "user@test.com",
                ),
            )
        }
    }

    @Test
    fun `verifyUsernameFindOtp returns username after successful otp verification`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())
        fixture.emailOtpPort.saveOtp(
            purpose = EmailOtpPurpose.USERNAME_RECOVERY,
            email = "user@test.com",
            otp = "123456",
            ttlSeconds = 300,
        )

        val result =
            fixture.accountRecoveryService.verifyUsernameFindOtp(
                VerifyUsernameFindOtpCommand(
                    studentNumber = 1101,
                    name = "테스터",
                    email = "user@test.com",
                    otp = "123456",
                ),
            )

        assertEquals("tester", result.username)
        assertFalse(fixture.emailOtpPort.hasOtp(EmailOtpPurpose.USERNAME_RECOVERY, "user@test.com"))
    }

    @Test
    fun `sendPasswordResetOtp sends otp when username and identity fields all match`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())

        fixture.accountRecoveryService.sendPasswordResetOtp(
            SendPasswordResetOtpCommand(
                username = "tester",
                studentNumber = 1101,
                name = "테스터",
                email = "user@test.com",
            ),
        )

        val sentEmail = fixture.emailSendPort.sentEmails.single()
        assertEquals("[Xquare] 비밀번호 재설정 코드", sentEmail.subject)
        assertNotNull(fixture.emailOtpPort.getOtp(EmailOtpPurpose.PASSWORD_RESET, "user@test.com"))
    }

    @Test
    fun `verifyPasswordResetOtp returns password reset token`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())
        fixture.emailOtpPort.saveOtp(
            purpose = EmailOtpPurpose.PASSWORD_RESET,
            email = "user@test.com",
            otp = "123456",
            ttlSeconds = 300,
        )

        val result =
            fixture.accountRecoveryService.verifyPasswordResetOtp(
                VerifyPasswordResetOtpCommand(
                    username = "tester",
                    studentNumber = 1101,
                    name = "테스터",
                    email = "user@test.com",
                    otp = "123456",
                ),
            )

        assertNotNull(
            fixture.emailOtpPort.getEmailByVerifiedToken(
                EmailOtpPurpose.PASSWORD_RESET,
                result.passwordResetToken,
            ),
        )
    }

    @Test
    fun `resetPassword changes password and consumes reset token`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())
        fixture.emailOtpPort.saveVerifiedToken(
            purpose = EmailOtpPurpose.PASSWORD_RESET,
            token = "reset-token",
            email = "user@test.com",
            ttlSeconds = 600,
        )

        fixture.accountRecoveryService.resetPassword(
            ResetPasswordCommand(
                passwordResetToken = "reset-token",
                newPassword = "new-password!",
            ),
        )

        assertEquals(
            "encoded:new-password!",
            fixture.userPersistencePort.findByEmail("user@test.com")?.password,
        )
        assertFalse(fixture.emailOtpPort.hasVerifiedToken(EmailOtpPurpose.PASSWORD_RESET, "reset-token"))
    }

    private fun createFixture(): Fixture {
        val emailOtpPort = FakeEmailOtpPort()
        val emailSendPort = FakeEmailSendPort()
        val emailOtpService =
            EmailOtpService(
                emailOtpPort = emailOtpPort,
                emailSendPort = emailSendPort,
                emailOtpProperties = emailOtpProperties(),
            )
        val userPersistencePort = FakeUserPersistenceForAccountRecoveryPort()

        val accountRecoveryService =
            AccountRecoveryService(
                userPersistencePort = userPersistencePort,
                passwordEncoderPort = FakePasswordEncoderPort(),
                emailOtpService = emailOtpService,
            )

        return Fixture(accountRecoveryService, userPersistencePort, emailOtpPort, emailSendPort)
    }

    private fun emailOtpProperties(): EmailOtpProperties =
        EmailOtpProperties(
            supportEmail = "support@test.com",
            templateName = "email/otp",
            expiresInText = "5분",
            otpTtlSeconds = 300,
            verifiedTokenTtlSeconds = 600,
            registerSubject = "[Xquare] 이메일 인증 코드",
            usernameRecoverySubject = "[Xquare] 아이디 찾기 코드",
            passwordResetSubject = "[Xquare] 비밀번호 재설정 코드",
        )

    private fun existingUser(): User =
        User(
            id = 1L,
            username = "tester",
            password = "encoded:old-password!",
            role = UserRole.MEMBER,
            studentNumber = 1101,
            name = "테스터",
            email = "user@test.com",
        )

    private data class Fixture(
        val accountRecoveryService: AccountRecoveryService,
        val userPersistencePort: FakeUserPersistenceForAccountRecoveryPort,
        val emailOtpPort: FakeEmailOtpPort,
        val emailSendPort: FakeEmailSendPort,
    )

    private class FakeUserPersistenceForAccountRecoveryPort : UserPersistenceForAccountRecoveryPort {
        private val users = linkedMapOf<Long, User>()
        private var nextId = 1L

        override fun save(user: User): User {
            val savedUser = user.copy(id = user.id ?: nextId++)
            users[savedUser.id!!] = savedUser
            return savedUser
        }

        override fun findByEmail(email: String): User? = users.values.firstOrNull { it.email == email }

        override fun findByStudentNumberAndNameAndEmail(
            studentNumber: Int,
            name: String,
            email: String,
        ): List<User> =
            users.values.filter {
                it.studentNumber == studentNumber && it.name == name && it.email == email
            }

        override fun findByUsernameAndStudentNumberAndNameAndEmail(
            username: String,
            studentNumber: Int,
            name: String,
            email: String,
        ): User? =
            users.values.firstOrNull {
                it.username == username && it.studentNumber == studentNumber && it.name == name && it.email == email
            }
    }

    private class FakePasswordEncoderPort : PasswordEncoderPort {
        override fun encode(password: String): String = "encoded:$password"

        override fun matches(
            password: String,
            encodedPassword: String,
        ): Boolean = encodedPassword == "encoded:$password"
    }

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
        private val verifiedTokens = mutableMapOf<Pair<EmailOtpPurpose, String>, String>()

        override fun saveOtp(
            purpose: EmailOtpPurpose,
            email: String,
            otp: String,
            ttlSeconds: Long,
        ) {
            otps[purpose to email] = otp
        }

        override fun getOtp(
            purpose: EmailOtpPurpose,
            email: String,
        ): String? = otps[purpose to email]

        override fun deleteOtp(
            purpose: EmailOtpPurpose,
            email: String,
        ) {
            otps.remove(purpose to email)
        }

        override fun saveVerifiedToken(
            purpose: EmailOtpPurpose,
            token: String,
            email: String,
            ttlSeconds: Long,
        ) {
            verifiedTokens[purpose to token] = email
        }

        override fun getEmailByVerifiedToken(
            purpose: EmailOtpPurpose,
            token: String,
        ): String? = verifiedTokens[purpose to token]

        override fun deleteVerifiedToken(
            purpose: EmailOtpPurpose,
            token: String,
        ) {
            verifiedTokens.remove(purpose to token)
        }

        fun hasOtp(
            purpose: EmailOtpPurpose,
            email: String,
        ): Boolean = otps.containsKey(purpose to email)

        fun hasVerifiedToken(
            purpose: EmailOtpPurpose,
            token: String,
        ): Boolean = verifiedTokens.containsKey(purpose to token)
    }

    private data class SentEmail(
        val to: String,
        val subject: String,
    )
}
