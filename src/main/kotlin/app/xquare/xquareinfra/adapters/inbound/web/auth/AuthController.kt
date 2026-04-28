package app.xquare.xquareinfra.adapters.inbound.web.auth

import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.EmailVerifiedTokenResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.LoginRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.RefreshTokenRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.RegisterRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.SendOtpRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.TokenResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.VerifyOtpRequestDto
import app.xquare.xquareinfra.application.auth.ports.inbound.LoginCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.LoginUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.RefreshTokenCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.RefreshTokenUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.SendEmailOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.SendEmailOtpUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.VerifyEmailOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.VerifyEmailOtpUseCase
import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth")
@SecurityRequirements
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val sendEmailOtpUseCase: SendEmailOtpUseCase,
    private val verifyEmailOtpUseCase: VerifyEmailOtpUseCase,
) {
    @Operation(summary = "이메일 OTP 발송")
    @PostMapping("/email/send")
    fun sendEmailOtp(
        @RequestBody @Valid request: SendOtpRequestDto,
    ): APiWrappedResponseDto<Unit> {
        sendEmailOtpUseCase.sendOtp(SendEmailOtpCommand(email = request.email))

        return APiWrappedResponseDto.success()
    }

    @Operation(summary = "이메일 OTP 검증")
    @PostMapping("/email/verify")
    fun verifyEmailOtp(
        @RequestBody @Valid request: VerifyOtpRequestDto,
    ): APiWrappedResponseDto<EmailVerifiedTokenResponseDto> {
        val result =
            verifyEmailOtpUseCase.verifyOtp(
                VerifyEmailOtpCommand(email = request.email, otp = request.otp),
            )

        return EmailVerifiedTokenResponseDto(result.emailVerifiedToken).toWrappedDto()
    }

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    fun register(
        @RequestBody @Valid request: RegisterRequestDto,
    ): APiWrappedResponseDto<TokenResponseDto> {
        val command =
            RegisterCommand(
                username = request.username,
                password = request.password,
                studentNumber = request.studentNumber,
                name = request.name,
                email = request.email,
                emailVerifiedToken = request.emailVerifiedToken,
            )

        val (accessToken, refreshToken) = registerUseCase.register(command)
        return TokenResponseDto(accessToken, refreshToken).toWrappedDto()
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    fun login(
        @RequestBody @Valid request: LoginRequestDto,
    ): APiWrappedResponseDto<TokenResponseDto> {
        val command =
            LoginCommand(
                username = request.username,
                password = request.password,
            )

        val (accessToken, refreshToken) = loginUseCase.login(command)
        return TokenResponseDto(accessToken, refreshToken).toWrappedDto()
    }

    @Operation(summary = "access token 재발급")
    @PostMapping("/refresh")
    fun refresh(
        @RequestBody @Valid request: RefreshTokenRequestDto,
    ): APiWrappedResponseDto<TokenResponseDto> {
        val command = RefreshTokenCommand(refreshToken = request.refreshToken)

        val (accessToken, refreshToken) = refreshTokenUseCase.refreshToken(command)
        return TokenResponseDto(accessToken, refreshToken).toWrappedDto()
    }
}
