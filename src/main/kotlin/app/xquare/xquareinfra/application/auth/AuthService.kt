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
import app.xquare.xquareinfra.application.auth.ports.outbound.AccessTokenPort
import app.xquare.xquareinfra.application.auth.ports.outbound.PasswordEncoderPort
import app.xquare.xquareinfra.application.auth.ports.outbound.RefreshTokenPort
import app.xquare.xquareinfra.application.auth.ports.outbound.UserPersistenceForAuthPort
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.domain.user.UserRole
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userPersistencePort: UserPersistenceForAuthPort,
    private val accessTokenPort: AccessTokenPort,
    private val refreshTokenPort: RefreshTokenPort,
    private val passwordEncoderPort: PasswordEncoderPort,
) : RegisterUseCase,
    LoginUseCase,
    RefreshTokenUseCase {
    override fun register(command: RegisterCommand): RegisterResult {
        val user =
            User(
                username = command.username,
                password = command.password,
                role = UserRole.MEMBER,
                studentNumber = command.studentNumber,
                name = command.name,
                email = command.email,
            )

        if (userPersistencePort.existsByUsername(user.username)) {
            return RegisterResult.UsernameAlreadyExists
        }

        val encodedPassword = passwordEncoderPort.encode(user.password)
        val passwordEncodedUser = user.copy(password = encodedPassword)

        val savedUser = userPersistencePort.save(passwordEncodedUser)

        val accessToken = accessTokenPort.create(savedUser.id!!)
        val refreshToken = refreshTokenPort.create(savedUser.id)

        return RegisterResult.Success(accessToken = accessToken, refreshToken = refreshToken)
    }

    override fun login(command: LoginCommand): LoginResult {
        val user =
            userPersistencePort.findByUsername(command.username)
                ?: return LoginResult.InvalidCredentials

        if (!passwordEncoderPort.matches(command.password, user.password)) {
            return LoginResult.InvalidCredentials
        }

        val accessToken = accessTokenPort.create(user.id!!)
        val refreshToken = refreshTokenPort.create(user.id)

        return LoginResult.Success(accessToken = accessToken, refreshToken = refreshToken)
    }

    override fun refreshToken(command: RefreshTokenCommand): RefreshTokenResult {
        if (!refreshTokenPort.isValid(command.refreshToken)) {
            return RefreshTokenResult.InvalidRefreshToken
        }

        val userId =
            refreshTokenPort.extractUserId(command.refreshToken)
                ?: return RefreshTokenResult.InvalidRefreshToken

        val newAccessToken = accessTokenPort.create(userId)
        val newRefreshToken = refreshTokenPort.create(userId)

        return RefreshTokenResult.Success(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
        )
    }
}
