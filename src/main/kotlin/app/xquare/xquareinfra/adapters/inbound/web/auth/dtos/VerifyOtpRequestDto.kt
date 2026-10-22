package app.xquare.xquareinfra.adapters.inbound.web.auth.dtos

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class VerifyOtpRequestDto(
    @field:Schema(
        description = "회원가입 이메일 인증 OTP를 받은 이메일 주소",
        example = "student@xquare.app",
    )
    @field:NotBlank
    @field:Email
    val email: String,
    @field:Schema(
        description = "이메일로 발송된 6자리 숫자 OTP",
        example = "123456",
    )
    @field:Pattern(regexp = "^\\d{6}$")
    @field:Size(min = 6, max = 6)
    @field:NotBlank
    val otp: String,
)
