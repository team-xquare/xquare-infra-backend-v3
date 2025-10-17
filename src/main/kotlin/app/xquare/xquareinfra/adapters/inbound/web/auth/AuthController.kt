package app.xquare.xquareinfra.adapters.inbound.web.auth

import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.LoginRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.RefreshTokenRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.RegisterRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.TokenResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.errorCode.AuthErrorCode
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

        val (accessToken, refreshToken) = registerUseCase.register(command)
        return ResponseEntity.ok(TokenResponseDto(accessToken, refreshToken).toWrappedDto())
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

        val (accessToken, refreshToken) = loginUseCase.login(command)
        return ResponseEntity.ok(TokenResponseDto(accessToken, refreshToken).toWrappedDto())
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody @Valid request: RefreshTokenRequestDto,
    ): ResponseEntity<*> {
        val command = RefreshTokenCommand(refreshToken = request.refreshToken)

        val (accessToken, refreshToken) = refreshTokenUseCase.refreshToken(command)
        return ResponseEntity.ok(TokenResponseDto(accessToken, refreshToken).toWrappedDto())
    }
}
