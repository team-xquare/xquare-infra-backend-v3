package app.xquare.xquareinfra.adapters.inbound.web.auth

import app.xquare.xquareinfra.application.auth.ports.inbound.LoginCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.LoginResult
import app.xquare.xquareinfra.application.auth.ports.inbound.LoginUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.RefreshTokenCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.RefreshTokenResult
import app.xquare.xquareinfra.application.auth.ports.inbound.RefreshTokenUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterResult
import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterUseCase
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import app.xquare.xquareinfra.infrastructure.web.toWrappedDto
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
) {
    @PostMapping("/register")
    fun register(
        @RequestBody @Valid request: RegisterRequestDto,
    ): ResponseEntity<*> {
        val command =
            RegisterCommand(
                username = request.username,
                password = request.password,
                studentNumber = request.studentNumber,
                name = request.name,
                email = request.email,
            )

        return when (val result = registerUseCase.register(command)) {
            is RegisterResult.Success ->
                ResponseEntity.ok(
                    TokenResponseDto(result.accessToken, result.refreshToken).toWrappedDto(),
                )
            is RegisterResult.UsernameAlreadyExists ->
                ResponseEntity.badRequest().body(
                    AuthErrorCode.USERNAME_ALREADY_EXISTS.toWrappedDto(),
                )
        }
    }

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid request: LoginRequestDto,
    ): ResponseEntity<*> {
        val command =
            LoginCommand(
                username = request.username,
                password = request.password,
            )

        return when (val result = loginUseCase.login(command)) {
            is LoginResult.Success ->
                ResponseEntity.ok(
                    TokenResponseDto(result.accessToken, result.refreshToken).toWrappedDto(),
                )
            is LoginResult.InvalidCredentials ->
                ResponseEntity.badRequest().body(
                    AuthErrorCode.INVALID_CREDENTIALS.toWrappedDto(),
                )
        }
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody @Valid request: RefreshTokenRequestDto,
    ): ResponseEntity<*> {
        val command = RefreshTokenCommand(refreshToken = request.refreshToken)

        return when (val result = refreshTokenUseCase.refreshToken(command)) {
            is RefreshTokenResult.Success ->
                ResponseEntity.ok(
                    TokenResponseDto(result.accessToken, result.refreshToken).toWrappedDto(),
                )
            is RefreshTokenResult.InvalidRefreshToken ->
                ResponseEntity.badRequest().body(
                    AuthErrorCode.INVALID_REFRESH_TOKEN.toWrappedDto(),
                )
        }
    }
}
