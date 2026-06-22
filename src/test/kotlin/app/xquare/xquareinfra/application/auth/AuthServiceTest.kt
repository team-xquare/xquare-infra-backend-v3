package app.xquare.xquareinfra.application.auth

import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.SendEmailOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.ResetPasswordCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.SendPasswordResetOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.SendUsernameFindOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.VerifyPasswordResetOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.VerifyUsernameFindOtpCommand
import app.xquare.xquareinfra.application.auth.ports.outbound.AccessTokenPort
import app.xquare.xquareinfra.application.auth.ports.outbound.RefreshTokenPort
import app.xquare.xquareinfra.application.auth.ports.outbound.UserPersistenceForAuthPort
import app.xquare.xquareinfra.application.emailOtp.EmailOtpProperties
import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import app.xquare.xquareinfra.application.emailOtp.EmailOtpService
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.testFixtures.FakeEmailOtpPort
import app.xquare.xquareinfra.testFixtures.FakeEmailSendPort
import app.xquare.xquareinfra.testFixtures.FakePasswordEncoderPort
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class AuthServiceTest {
    @Test
    fun `sendOtp sends register otp email`() {
        val fixture = createFixture()

        fixture.authService.sendOtp(SendEmailOtpCommand(email = "user@test.com"))

        val sentEmail = fixture.emailSendPort.sentEmails.single()
        assertEquals("[Xquare] 이메일 인증 코드", sentEmail.subject)
        assertNotNull(fixture.emailOtpPort.getOtp(EmailOtpPurpose.REGISTER, "user@test.com"))
    }

    @Test
    fun `register saves user and consumes verified token`() {
        val fixture = createFixture()
        fixture.emailOtpPort.saveVerifiedToken(
            purpose = EmailOtpPurpose.REGISTER,
            token = "verified-token",
            email = "user@test.com",
            ttlSeconds = 600,
        )

        val result =
            fixture.authService.register(
                RegisterCommand(
                    username = "tester",
                    password = "password!",
                    studentNumber = 1101,
                    name = "테스터",
                    email = "user@test.com",
                    emailVerifiedToken = "verified-token",
                ),
            )

        val savedUser = fixture.userPersistencePort.findByUsername("tester")
        assertEquals("encoded:password!", savedUser?.password)
        assertEquals("access-1", result.accessToken)
        assertEquals("refresh-1", result.refreshToken)
        assertFalse(fixture.emailOtpPort.hasVerifiedToken(EmailOtpPurpose.REGISTER, "verified-token"))
    }

    @Test
    fun `register throws when email already exists`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(
            User(
                username = "existing-user",
                password = "encoded:password!",
                role = app.xquare.xquareinfra.domain.user.UserRole.MEMBER,
                studentNumber = 1101,
                name = "기존유저",
                email = "user@test.com",
            ),
        )
        fixture.emailOtpPort.saveVerifiedToken(
            purpose = EmailOtpPurpose.REGISTER,
            token = "verified-token",
            email = "user@test.com",
            ttlSeconds = 600,
        )

        assertThrows<AuthException.EmailAlreadyExists> {
            fixture.authService.register(
                RegisterCommand(
                    username = "tester",
                    password = "password!",
                    studentNumber = 1102,
                    name = "테스터",
                    email = "user@test.com",
                    emailVerifiedToken = "verified-token",
                ),
            )
        }
    }

    @Test
    fun `register keeps verified token when email does not match token owner`() {
        val fixture = createFixture()
        fixture.emailOtpPort.saveVerifiedToken(
            purpose = EmailOtpPurpose.REGISTER,
            token = "verified-token",
            email = "user@test.com",
            ttlSeconds = 600,
        )

        assertThrows<AuthException.EmailNotVerified> {
            fixture.authService.register(
                RegisterCommand(
                    username = "tester",
                    password = "password!",
                    studentNumber = 1101,
                    name = "테스터",
                    email = "other@test.com",
                    emailVerifiedToken = "verified-token",
                ),
            )
        }

        assertNotNull(
            fixture.emailOtpPort.consumeVerifiedToken(
                EmailOtpPurpose.REGISTER,
                "verified-token",
            ),
        )
    }

    @Test
    fun `sendUsernameFindOtp sends otp when studentNumber name email all match`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())

        fixture.authService.sendUsernameFindOtp(
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
    fun `sendUsernameFindOtp does nothing when identity fields do not match`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())

        fixture.authService.sendUsernameFindOtp(
            SendUsernameFindOtpCommand(
                studentNumber = 1101,
                name = "다른이름",
                email = "user@test.com",
            ),
        )

        assertEquals(emptyList(), fixture.emailSendPort.sentEmails)
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
            fixture.authService.verifyUsernameFindOtp(
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

        fixture.authService.sendPasswordResetOtp(
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
    fun `sendPasswordResetOtp does nothing when username and identity fields do not match`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())

        fixture.authService.sendPasswordResetOtp(
            SendPasswordResetOtpCommand(
                username = "other-user",
                studentNumber = 1101,
                name = "테스터",
                email = "user@test.com",
            ),
        )

        assertEquals(emptyList(), fixture.emailSendPort.sentEmails)
    }

    @Test
    fun `recovery otp send endpoints rate limit existing and unknown identities equally`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())

        repeat(3) {
            fixture.authService.sendUsernameFindOtp(
                SendUsernameFindOtpCommand(
                    studentNumber = 1102,
                    name = "테스터",
                    email = "User@Test.com",
                ),
            )
            fixture.authService.sendPasswordResetOtp(
                SendPasswordResetOtpCommand(
                    username = "tester",
                    studentNumber = 1101,
                    name = "테스터",
                    email = "User@Test.com",
                ),
            )
        }

        assertThrows<AuthException.OtpRateLimitExceeded> {
            fixture.authService.sendUsernameFindOtp(
                SendUsernameFindOtpCommand(
                    studentNumber = 1101,
                    name = "테스터",
                    email = "user@test.com",
                ),
            )
        }
        assertThrows<AuthException.OtpRateLimitExceeded> {
            fixture.authService.sendPasswordResetOtp(
                SendPasswordResetOtpCommand(
                    username = "unknown-user",
                    studentNumber = 1101,
                    name = "테스터",
                    email = "user@test.com",
                ),
            )
        }
        assertEquals(3, fixture.emailSendPort.sentEmails.size)
    }

    @Test
    fun `account recovery flow ignores email case end to end`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())

        fixture.authService.sendUsernameFindOtp(
            SendUsernameFindOtpCommand(
                studentNumber = 1101,
                name = "테스터",
                email = "USER@Test.com",
            ),
        )
        val usernameOtp =
            fixture.emailOtpPort.getOtp(EmailOtpPurpose.USERNAME_RECOVERY, "user@test.com")
                ?: error("username recovery otp was not saved")
        val usernameResult =
            fixture.authService.verifyUsernameFindOtp(
                VerifyUsernameFindOtpCommand(
                    studentNumber = 1101,
                    name = "테스터",
                    email = "User@Test.com",
                    otp = usernameOtp,
                ),
            )

        fixture.authService.sendPasswordResetOtp(
            SendPasswordResetOtpCommand(
                username = "tester",
                studentNumber = 1101,
                name = "테스터",
                email = "USER@Test.com",
            ),
        )
        val passwordOtp =
            fixture.emailOtpPort.getOtp(EmailOtpPurpose.PASSWORD_RESET, "user@test.com")
                ?: error("password reset otp was not saved")
        val passwordResult =
            fixture.authService.verifyPasswordResetOtp(
                VerifyPasswordResetOtpCommand(
                    username = "tester",
                    studentNumber = 1101,
                    name = "테스터",
                    email = "User@Test.com",
                    otp = passwordOtp,
                ),
            )
        fixture.authService.resetPassword(
            ResetPasswordCommand(
                passwordResetToken = passwordResult.passwordResetToken,
                newPassword = "new-password!",
            ),
        )

        assertEquals(2, fixture.emailSendPort.sentEmails.size)
        assertEquals("tester", usernameResult.username)
        assertEquals("encoded:new-password!", fixture.userPersistencePort.findByEmail("USER@Test.com")?.password)
    }

    @Test
    fun `username recovery verification hides whether identity or otp failed`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())
        fixture.emailOtpPort.saveOtp(
            purpose = EmailOtpPurpose.USERNAME_RECOVERY,
            email = "user@test.com",
            otp = "123456",
            ttlSeconds = 300,
        )

        assertThrows<AuthException.InvalidUserInfo> {
            fixture.authService.verifyUsernameFindOtp(
                VerifyUsernameFindOtpCommand(
                    studentNumber = 1102,
                    name = "테스터",
                    email = "user@test.com",
                    otp = "123456",
                ),
            )
        }
        assertThrows<AuthException.InvalidUserInfo> {
            fixture.authService.verifyUsernameFindOtp(
                VerifyUsernameFindOtpCommand(
                    studentNumber = 1101,
                    name = "테스터",
                    email = "user@test.com",
                    otp = "000000",
                ),
            )
        }
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
            fixture.authService.verifyPasswordResetOtp(
                VerifyPasswordResetOtpCommand(
                    username = "tester",
                    studentNumber = 1101,
                    name = "테스터",
                    email = "user@test.com",
                    otp = "123456",
                ),
            )

        assertNotNull(
            fixture.emailOtpPort.consumeVerifiedToken(
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

        fixture.authService.resetPassword(
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
        val userPersistencePort = FakeUserPersistenceForAuthPort()

        val authService =
            AuthService(
                userPersistencePort = userPersistencePort,
                accessTokenPort = FakeAccessTokenPort(),
                refreshTokenPort = FakeRefreshTokenPort(),
                passwordEncoderPort = FakePasswordEncoderPort(),
                emailOtpService = emailOtpService,
            )

        return Fixture(authService, userPersistencePort, emailOtpPort, emailSendPort)
    }

    private fun emailOtpProperties(): EmailOtpProperties =
        EmailOtpProperties(
            supportEmail = "support@test.com",
            templateName = "email/otp",
            expiresInText = "5분",
            otpTtlSeconds = 300,
            verifiedTokenTtlSeconds = 600,
            sendRateLimitMaxRequests = 3,
            sendRateLimitWindowSeconds = 300,
            registerSubject = "[Xquare] 이메일 인증 코드",
            usernameRecoverySubject = "[Xquare] 아이디 찾기 코드",
            passwordResetSubject = "[Xquare] 비밀번호 재설정 코드",
        )

    private fun existingUser(): User =
        User(
            id = 1L,
            username = "tester",
            password = "encoded:old-password!",
            role = app.xquare.xquareinfra.domain.user.UserRole.MEMBER,
            studentNumber = 1101,
            name = "테스터",
            email = "user@test.com",
        )

    private data class Fixture(
        val authService: AuthService,
        val userPersistencePort: FakeUserPersistenceForAuthPort,
        val emailOtpPort: FakeEmailOtpPort,
        val emailSendPort: FakeEmailSendPort,
    )

    private class FakeUserPersistenceForAuthPort : UserPersistenceForAuthPort {
        private val users = linkedMapOf<Long, User>()
        private var nextId = 1L

        override fun existsByUsername(username: String): Boolean = users.values.any { it.username == username }

        override fun existsByEmail(email: String): Boolean = users.values.any { it.email.equals(email, ignoreCase = true) }

        override fun save(user: User): User {
            val savedUser = user.copy(id = user.id ?: nextId++)
            users[savedUser.id!!] = savedUser
            return savedUser
        }

        override fun findByEmail(email: String): User? = users.values.firstOrNull { it.email.equals(email, ignoreCase = true) }

        override fun findByStudentNumberAndNameAndEmail(
            studentNumber: Int,
            name: String,
            email: String,
        ): List<User> =
            users.values.filter {
                it.studentNumber == studentNumber && it.name == name && it.email.equals(email, ignoreCase = true)
            }

        override fun findByUsernameAndStudentNumberAndNameAndEmail(
            username: String,
            studentNumber: Int,
            name: String,
            email: String,
        ): User? =
            users.values.firstOrNull {
                it.username == username &&
                    it.studentNumber == studentNumber &&
                    it.name == name &&
                    it.email.equals(email, ignoreCase = true)
            }

        override fun findByUsername(username: String): User? = users.values.firstOrNull { it.username == username }
    }

    private class FakeAccessTokenPort : AccessTokenPort {
        override fun create(userId: Long): String = "access-$userId"
    }

    private class FakeRefreshTokenPort : RefreshTokenPort {
        override fun create(userId: Long): String = "refresh-$userId"

        override fun isValid(refreshToken: String): Boolean = true

        override fun extractUserId(refreshToken: String): Long = 1L
    }
}
