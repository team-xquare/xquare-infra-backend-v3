package app.xquare.xquareinfra.application.accountRecovery

import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.ResetPasswordCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.ResetPasswordUseCase
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendPasswordResetOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendPasswordResetOtpUseCase
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendUsernameFindOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendUsernameFindOtpUseCase
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyPasswordResetOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyPasswordResetOtpResult
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyPasswordResetOtpUseCase
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyUsernameFindOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyUsernameFindOtpResult
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyUsernameFindOtpUseCase
import app.xquare.xquareinfra.application.accountRecovery.ports.outbound.UserPersistenceForAccountRecoveryPort
import app.xquare.xquareinfra.application.auth.AuthException
import app.xquare.xquareinfra.application.auth.ports.outbound.PasswordEncoderPort
import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import app.xquare.xquareinfra.application.emailOtp.EmailOtpService
import app.xquare.xquareinfra.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AccountRecoveryService(
    private val userPersistencePort: UserPersistenceForAccountRecoveryPort,
    private val passwordEncoderPort: PasswordEncoderPort,
    private val emailOtpService: EmailOtpService,
) : SendUsernameFindOtpUseCase,
    SendPasswordResetOtpUseCase,
    VerifyPasswordResetOtpUseCase,
    ResetPasswordUseCase,
    VerifyUsernameFindOtpUseCase {
    override fun sendUsernameFindOtp(command: SendUsernameFindOtpCommand) {
        val user = userPersistencePort.findByStudentNumberAndNameAndEmail(command.studentNumber, command.name, command.email)
            .singleOrNull()
            ?: return
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

    override fun sendPasswordResetOtp(command: SendPasswordResetOtpCommand) {
        val user =
            getUserByUsernameAndStudentNumberAndNameAndEmail(
                command.username,
                command.studentNumber,
                command.name,
                command.email,
            )
        emailOtpService.sendOtp(user.email, EmailOtpPurpose.PASSWORD_RESET)
    }

    override fun verifyPasswordResetOtp(command: VerifyPasswordResetOtpCommand): VerifyPasswordResetOtpResult {
        getUserByUsernameAndStudentNumberAndNameAndEmail(
            command.username,
            command.studentNumber,
            command.name,
            command.email,
        )

        val passwordResetToken =
            emailOtpService.verifyOtpAndIssueVerifiedToken(
                email = command.email,
                otp = command.otp,
                purpose = EmailOtpPurpose.PASSWORD_RESET,
            )

        return VerifyPasswordResetOtpResult(passwordResetToken = passwordResetToken)
    }

    override fun resetPassword(command: ResetPasswordCommand) {
        val email =
            emailOtpService.getVerifiedEmail(command.passwordResetToken, EmailOtpPurpose.PASSWORD_RESET)
                ?: throw AuthException.PasswordResetTokenNotFound
        val user = userPersistencePort.findByEmail(email) ?: throw AuthException.PasswordResetTokenNotFound
        val encodedPassword = passwordEncoderPort.encode(command.newPassword)

        userPersistencePort.save(user.copy(password = encodedPassword))
        emailOtpService.deleteVerifiedToken(command.passwordResetToken, EmailOtpPurpose.PASSWORD_RESET)
    }

    private fun getUserByStudentNumberAndNameAndEmail(
        studentNumber: Int,
        name: String,
        email: String,
    ): User =
        userPersistencePort.findByStudentNumberAndNameAndEmail(studentNumber, name, email).singleOrNull()
            ?: throw AuthException.InvalidUserInfo

    private fun getUserByUsernameAndStudentNumberAndNameAndEmail(
        username: String,
        studentNumber: Int,
        name: String,
        email: String,
    ): User =
        userPersistencePort.findByUsernameAndStudentNumberAndNameAndEmail(username, studentNumber, name, email)
            ?: throw AuthException.InvalidUserInfo
}
