package app.xquare.xquareinfra.adapters.inbound.web.accountRecovery

import app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos.SendUsernameFindOtpRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos.UsernameResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.accountRecovery.dtos.VerifyUsernameFindOtpRequestDto
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendUsernameFindOtpCommand
import app.xquare.xquareinfra.application.accountRecovery.ports.inbound.SendUsernameFindOtpUseCase
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
}
