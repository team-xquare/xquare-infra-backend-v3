package app.xquare.xquareinfra.application.auth

import app.xquare.xquareinfra.application.auth.ports.inbound.LoginCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.LoginResult
import app.xquare.xquareinfra.application.auth.ports.inbound.LoginUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.RefreshTokenCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.RefreshTokenResult
import app.xquare.xquareinfra.application.auth.ports.inbound.RefreshTokenUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterResult
import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.SendEmailOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.SendEmailOtpUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.VerifyEmailOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.VerifyEmailOtpResult
import app.xquare.xquareinfra.application.auth.ports.inbound.VerifyEmailOtpUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.ResetPasswordCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.ResetPasswordUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.SendPasswordResetOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.SendPasswordResetOtpUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.SendUsernameFindOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.SendUsernameFindOtpUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.VerifyPasswordResetOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.VerifyPasswordResetOtpResult
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.VerifyPasswordResetOtpUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.VerifyUsernameFindOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.VerifyUsernameFindOtpResult
import app.xquare.xquareinfra.application.auth.ports.inbound.recovery.VerifyUsernameFindOtpUseCase
import app.xquare.xquareinfra.application.auth.ports.outbound.AccessTokenPort
import app.xquare.xquareinfra.application.auth.ports.outbound.PasswordEncoderPort
import app.xquare.xquareinfra.application.auth.ports.outbound.RefreshTokenPort
import app.xquare.xquareinfra.application.auth.ports.outbound.UserPersistenceForAuthPort
import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import app.xquare.xquareinfra.application.emailOtp.EmailOtpService
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.domain.user.UserRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class AuthService(
    private val userPersistencePort: UserPersistenceForAuthPort,
    private val accessTokenPort: AccessTokenPort,
    private val refreshTokenPort: RefreshTokenPort,
    private val passwordEncoderPort: PasswordEncoderPort,
    private val emailOtpService: EmailOtpService,
) : RegisterUseCase,
    LoginUseCase,
    RefreshTokenUseCase,
    SendEmailOtpUseCase,
    VerifyEmailOtpUseCase,
    SendUsernameFindOtpUseCase,
    VerifyUsernameFindOtpUseCase,
    SendPasswordResetOtpUseCase,
    VerifyPasswordResetOtpUseCase,
    ResetPasswordUseCase {
    override fun register(command: RegisterCommand): RegisterResult {
        emailOtpService.consumeVerifiedEmail(
            command.emailVerifiedToken,
            EmailOtpPurpose.REGISTER,
            command.email,
        )
            ?: throw AuthException.EmailNotVerified

        if (userPersistencePort.existsByEmail(command.email)) {
            throw AuthException.EmailAlreadyExists
        }

        if (userPersistencePort.existsByUsername(command.username)) {
            throw AuthException.UsernameAlreadyExists
        }

        val encodedPassword = passwordEncoderPort.encode(command.password)
        val user =
            User(
                username = command.username,
                password = encodedPassword,
                role = UserRole.MEMBER,
                studentNumber = command.studentNumber,
                name = command.name,
                email = command.email,
            )

        val savedUser = userPersistencePort.save(user)
        val accessToken = accessTokenPort.create(savedUser.id!!)
        val refreshToken = refreshTokenPort.create(savedUser.id)

        return RegisterResult(accessToken = accessToken, refreshToken = refreshToken)
    }

    override fun sendOtp(command: SendEmailOtpCommand) = emailOtpService.sendOtp(command.email, EmailOtpPurpose.REGISTER)

    override fun verifyOtp(command: VerifyEmailOtpCommand): VerifyEmailOtpResult {
        val verifiedToken =
            emailOtpService.verifyOtpAndIssueVerifiedToken(
                email = command.email,
                otp = command.otp,
                purpose = EmailOtpPurpose.REGISTER,
            )

        return VerifyEmailOtpResult(emailVerifiedToken = verifiedToken)
    }

    override fun login(command: LoginCommand): LoginResult {
        val user =
            userPersistencePort.findByUsername(command.username)
                ?: throw AuthException.InvalidCredentials

        if (!passwordEncoderPort.matches(command.password, user.password)) {
            throw AuthException.InvalidCredentials
        }

        val accessToken = accessTokenPort.create(user.id!!)
        val refreshToken = refreshTokenPort.create(user.id)

        return LoginResult(accessToken = accessToken, refreshToken = refreshToken)
    }

    override fun refreshToken(command: RefreshTokenCommand): RefreshTokenResult {
        if (!refreshTokenPort.isValid(command.refreshToken)) {
            throw AuthException.InvalidRefreshToken
        }

        val userId =
            refreshTokenPort.extractUserId(command.refreshToken)
                ?: throw AuthException.InvalidRefreshToken

        val newAccessToken = accessTokenPort.create(userId)
        val newRefreshToken = refreshTokenPort.create(userId)

        return RefreshTokenResult(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
        )
    }

    override fun sendUsernameFindOtp(command: SendUsernameFindOtpCommand) {
        val user =
            userPersistencePort
                .findByStudentNumberAndNameAndEmail(command.studentNumber, command.name, command.email)
                .singleOrNull()

        emailOtpService.sendOtp(
            email = command.email,
            purpose = EmailOtpPurpose.USERNAME_RECOVERY,
            recipientEmail = user?.email,
        )
    }

    override fun verifyUsernameFindOtp(command: VerifyUsernameFindOtpCommand): VerifyUsernameFindOtpResult =
        withUnifiedRecoveryFailure {
            val user = getUserByStudentNumberAndNameAndEmail(command.studentNumber, command.name, command.email)
            emailOtpService.verifyOtp(
                email = user.email,
                otp = command.otp,
                purpose = EmailOtpPurpose.USERNAME_RECOVERY,
            )

            VerifyUsernameFindOtpResult(username = user.username)
        }

    override fun sendPasswordResetOtp(command: SendPasswordResetOtpCommand) {
        val user =
            userPersistencePort.findByUsernameAndStudentNumberAndNameAndEmail(
                command.username,
                command.studentNumber,
                command.name,
                command.email,
            )

        emailOtpService.sendOtp(
            email = command.email,
            purpose = EmailOtpPurpose.PASSWORD_RESET,
            recipientEmail = user?.email,
        )
    }

    override fun verifyPasswordResetOtp(command: VerifyPasswordResetOtpCommand): VerifyPasswordResetOtpResult =
        withUnifiedRecoveryFailure {
            val user =
                getUserByUsernameAndStudentNumberAndNameAndEmail(
                    command.username,
                    command.studentNumber,
                    command.name,
                    command.email,
                )

            val passwordResetToken =
                emailOtpService.verifyOtpAndIssueVerifiedToken(
                    email = user.email,
                    otp = command.otp,
                    purpose = EmailOtpPurpose.PASSWORD_RESET,
                )

            VerifyPasswordResetOtpResult(passwordResetToken = passwordResetToken)
        }

    override fun resetPassword(command: ResetPasswordCommand) {
        val email =
            emailOtpService.consumeVerifiedEmail(command.passwordResetToken, EmailOtpPurpose.PASSWORD_RESET)
                ?: throw AuthException.PasswordResetTokenNotFound
        val user = userPersistencePort.findByEmail(email) ?: throw AuthException.PasswordResetTokenNotFound
        val encodedPassword = passwordEncoderPort.encode(command.newPassword)

        userPersistencePort.save(user.copy(password = encodedPassword))
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

    private fun <T> withUnifiedRecoveryFailure(block: () -> T): T =
        try {
            block()
        } catch (ex: AuthException) {
            when (ex) {
                AuthException.InvalidUserInfo,
                AuthException.OtpMismatch,
                AuthException.OtpNotFound,
                -> throw AuthException.InvalidUserInfo

                else -> throw ex
            }
        }
}
