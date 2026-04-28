package app.xquare.xquareinfra.application.accountRecovery

import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendUsernameFindOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendUsernameFindOtpUseCase
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyUsernameFindOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyUsernameFindOtpResult
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyUsernameFindOtpUseCase
import app.xquare.xquareinfra.application.accountRecovery.ports.outbound.UserPersistenceForAccountRecoveryPort
import app.xquare.xquareinfra.application.auth.AuthException
import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import app.xquare.xquareinfra.application.emailOtp.EmailOtpService
import app.xquare.xquareinfra.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AccountRecoveryService(
    private val userPersistencePort: UserPersistenceForAccountRecoveryPort,
    private val emailOtpService: EmailOtpService,
) : SendUsernameFindOtpUseCase,
    VerifyUsernameFindOtpUseCase {
    override fun sendUsernameFindOtp(command: SendUsernameFindOtpCommand) {
        val user = getUserByStudentNumberAndNameAndEmail(command.studentNumber, command.name, command.email)
        emailOtpService.sendOtp(user.email, EmailOtpPurpose.USERNAME_RECOVERY)
    }

    override fun verifyUsernameFindOtp(command: VerifyUsernameFindOtpCommand): VerifyUsernameFindOtpResult {
        val user = getUserByStudentNumberAndNameAndEmail(command.studentNumber, command.name, command.email)
        emailOtpService.verifyOtp(
            email = command.email,
            otp = command.otp,
            purpose = EmailOtpPurpose.USERNAME_RECOVERY,
        )

        return VerifyUsernameFindOtpResult(username = user.username)
    }

    private fun getUserByStudentNumberAndNameAndEmail(
        studentNumber: Int,
        name: String,
        email: String,
    ): User =
        userPersistencePort.findByStudentNumberAndNameAndEmail(studentNumber, name, email).singleOrNull()
            ?: throw AuthException.InvalidUserInfo
}
