package app.xquare.xquareinfra.application.auth

import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.SendEmailOtpCommand
import app.xquare.xquareinfra.application.auth.ports.outbound.AccessTokenPort
import app.xquare.xquareinfra.application.auth.ports.outbound.PasswordEncoderPort
import app.xquare.xquareinfra.application.auth.ports.outbound.RefreshTokenPort
import app.xquare.xquareinfra.application.auth.ports.outbound.UserPersistenceForAuthPort
import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import app.xquare.xquareinfra.application.emailOtp.EmailOtpProperties
import app.xquare.xquareinfra.application.emailOtp.EmailOtpService
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailOtpPort
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailSendPort
import app.xquare.xquareinfra.application.emailOtp.ports.outbound.OtpConsumeResult
import app.xquare.xquareinfra.domain.user.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import org.junit.jupiter.api.assertThrows

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
            registerSubject = "[Xquare] 이메일 인증 코드",
            usernameRecoverySubject = "[Xquare] 아이디 찾기 코드",
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

        override fun existsByEmail(email: String): Boolean = users.values.any { it.email == email }

        override fun save(user: User): User {
            val savedUser = user.copy(id = user.id ?: nextId++)
            users[savedUser.id!!] = savedUser
            return savedUser
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
            return OtpConsumeResult.CONSUMED
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
