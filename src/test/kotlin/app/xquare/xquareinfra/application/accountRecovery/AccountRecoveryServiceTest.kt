package app.xquare.xquareinfra.application.accountRecovery

import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendUsernameFindOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyUsernameFindOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.outbound.UserPersistenceForAccountRecoveryPort
import app.xquare.xquareinfra.application.auth.AuthException
import app.xquare.xquareinfra.application.emailOtp.EmailOtpProperties
import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import app.xquare.xquareinfra.application.emailOtp.EmailOtpService
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.domain.user.UserRole
import app.xquare.xquareinfra.testFixtures.FakeEmailOtpPort
import app.xquare.xquareinfra.testFixtures.FakeEmailSendPort
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

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
    fun `sendUsernameFindOtp does nothing when identity fields do not match`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())

        fixture.accountRecoveryService.sendUsernameFindOtp(
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
    fun `account recovery lookups ignore email case`() {
        val fixture = createFixture()
        fixture.userPersistencePort.save(existingUser())

        fixture.accountRecoveryService.sendUsernameFindOtp(
            SendUsernameFindOtpCommand(
                studentNumber = 1101,
                name = "테스터",
                email = "USER@Test.com",
            ),
        )

        assertEquals(1, fixture.emailSendPort.sentEmails.size)
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

        fun save(user: User): User {
            val savedUser = user.copy(id = user.id ?: nextId++)
            users[savedUser.id!!] = savedUser
            return savedUser
        }

        override fun findByStudentNumberAndNameAndEmail(
            studentNumber: Int,
            name: String,
            email: String,
        ): List<User> =
            users.values.filter {
                it.studentNumber == studentNumber && it.name == name && it.email.equals(email, ignoreCase = true)
            }

    }
}
