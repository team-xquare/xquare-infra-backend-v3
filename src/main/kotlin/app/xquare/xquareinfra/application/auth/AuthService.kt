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
import app.xquare.xquareinfra.application.auth.ports.outbound.AccessTokenPort
import app.xquare.xquareinfra.application.auth.ports.outbound.EmailOtpPort
import app.xquare.xquareinfra.application.auth.ports.outbound.EmailSendPort
import app.xquare.xquareinfra.application.auth.ports.outbound.PasswordEncoderPort
import app.xquare.xquareinfra.application.auth.ports.outbound.RefreshTokenPort
import app.xquare.xquareinfra.application.auth.ports.outbound.UserPersistenceForAuthPort
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.domain.user.UserRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class AuthService(
    private val userPersistencePort: UserPersistenceForAuthPort,
    private val accessTokenPort: AccessTokenPort,
    private val refreshTokenPort: RefreshTokenPort,
    private val passwordEncoderPort: PasswordEncoderPort,
    private val emailSendPort: EmailSendPort,
    private val emailOtpPort: EmailOtpPort
) : RegisterUseCase,
    LoginUseCase,
    RefreshTokenUseCase,
    SendEmailOtpUseCase,
    VerifyEmailOtpUseCase {
    override fun register(command: RegisterCommand): RegisterResult {
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
        emailOtpPort.deleteVerifiedToken(command.emailVerifiedToken)

        val accessToken = accessTokenPort.create(savedUser.id!!)
        val refreshToken = refreshTokenPort.create(savedUser.id)

        return RegisterResult(accessToken = accessToken, refreshToken = refreshToken)
    }

    override fun sendOtp(commend: SendEmailOtpCommand) {
        val otp = (100000..999999).random().toString()
        emailOtpPort.saveOtp(commend.email, otp, ttlSeconds = 300)

        emailSendPort.send(
            to = commend.email,
            subject = "Xquare 이메일 인증 코드",
            body = "인증 코드: $otp\n5분 이내에 입력해주세.",
        )
    }

    override fun verifyOtp(commend: VerifyEmailOtpCommand): VerifyEmailOtpResult {
        val savedOtp = emailOtpPort.getOtp(commend.email)
            ?: throw AuthException.OtpNotFound

        if (savedOtp != commend.otp) {
            throw AuthException.OtpMismatch
        }

        emailOtpPort.deleteOtp(commend.email)

        val verifiedToken = UUID.randomUUID().toString()
        emailOtpPort.saveVerifiedToken(verifiedToken, commend.email, ttlSeconds = 600)

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
}
