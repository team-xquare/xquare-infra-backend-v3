package app.xquare.xquareinfra.adapters.inbound.web.accountRecovery

import app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos.PasswordResetTokenResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos.ResetPasswordRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos.SendPasswordResetOtpRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos.SendUsernameFindOtpRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos.UsernameResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos.VerifyPasswordResetOtpRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos.VerifyUsernameFindOtpRequestDto
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.ResetPasswordCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.ResetPasswordUseCase
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendPasswordResetOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendPasswordResetOtpUseCase
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendUsernameFindOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendUsernameFindOtpUseCase
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyPasswordResetOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyPasswordResetOtpUseCase
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyUsernameFindOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.VerifyUsernameFindOtpUseCase
import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Account Recovery")
@SecurityRequirements
@RestController
@RequestMapping("/api/v1/account-recovery")
class AccountRecoveryController(
    private val sendUsernameFindOtpUseCase: SendUsernameFindOtpUseCase,
    private val verifyUsernameFindOtpUseCase: VerifyUsernameFindOtpUseCase,
    private val sendPasswordResetOtpUseCase: SendPasswordResetOtpUseCase,
    private val verifyPasswordResetOtpUseCase: VerifyPasswordResetOtpUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
) {
    @Operation(summary = "아이디 찾기 OTP 발송")
    @PostMapping("/username/email/send")
    fun sendUsernameFindOtp(
        @RequestBody @Valid request: SendUsernameFindOtpRequestDto,
    ): APiWrappedResponseDto<Unit> {
        sendUsernameFindOtpUseCase.sendUsernameFindOtp(
            SendUsernameFindOtpCommand(
                studentNumber = request.studentNumber,
                name = request.name,
                email = request.email,
            ),
        )

        return APiWrappedResponseDto.success()
    }

    @Operation(summary = "아이디 찾기 OTP 검증")
    @PostMapping("/username/email/verify")
    fun verifyUsernameFindOtp(
        @RequestBody @Valid request: VerifyUsernameFindOtpRequestDto,
    ): APiWrappedResponseDto<UsernameResponseDto> {
        val result =
            verifyUsernameFindOtpUseCase.verifyUsernameFindOtp(
                VerifyUsernameFindOtpCommand(
                    studentNumber = request.studentNumber,
                    name = request.name,
                    email = request.email,
                    otp = request.otp,
                ),
            )

        return UsernameResponseDto(result.username).toWrappedDto()
    }

    @Operation(summary = "비밀번호 재설정 아이디 확인 및 OTP 발송")
    @PostMapping("/password/email/send")
    fun sendPasswordResetOtp(
        @RequestBody @Valid request: SendPasswordResetOtpRequestDto,
    ): APiWrappedResponseDto<Unit> {
        sendPasswordResetOtpUseCase.sendPasswordResetOtp(
            SendPasswordResetOtpCommand(
                username = request.username,
                studentNumber = request.studentNumber,
                name = request.name,
                email = request.email,
            ),
        )

        return APiWrappedResponseDto.success()
    }

    @Operation(summary = "비밀번호 재설정 OTP 검증")
    @PostMapping("/password/email/verify")
    fun verifyPasswordResetOtp(
        @RequestBody @Valid request: VerifyPasswordResetOtpRequestDto,
    ): APiWrappedResponseDto<PasswordResetTokenResponseDto> {
        val result =
            verifyPasswordResetOtpUseCase.verifyPasswordResetOtp(
                VerifyPasswordResetOtpCommand(
                    username = request.username,
                    studentNumber = request.studentNumber,
                    name = request.name,
                    email = request.email,
                    otp = request.otp,
                ),
            )

        return PasswordResetTokenResponseDto(result.passwordResetToken).toWrappedDto()
    }

    @Operation(summary = "비밀번호 재설정")
    @PatchMapping("/password")
    fun resetPassword(
        @RequestBody @Valid request: ResetPasswordRequestDto,
    ): APiWrappedResponseDto<Unit> {
        resetPasswordUseCase.resetPassword(
            ResetPasswordCommand(
                passwordResetToken = request.passwordResetToken,
                newPassword = request.newPassword,
            ),
        )

        return APiWrappedResponseDto.success()
    }
}
