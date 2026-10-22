package app.xquare.xquareinfra.adapters.inbound.web.auth.dtos

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SendOtpRequestDto(
    @field:Schema(
        description = "회원가입 이메일 인증 OTP를 받을 이메일 주소",
        example = "student@xquare.app",
    )
    @field:Email
    @field:NotBlank
    val email: String,
)
