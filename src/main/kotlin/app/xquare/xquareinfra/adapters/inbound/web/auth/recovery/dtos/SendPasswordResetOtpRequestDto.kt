package app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SendPasswordResetOtpRequestDto(
    @field:Schema(
        description = "비밀번호를 재설정할 사용자 아이디",
        example = "xquare_user",
    )
    @field:Size(min = 4, max = 15)
    @field:Pattern(
        regexp = "^[A-Za-z0-9_-]+$",
    )
    val username: String,
    @field:Schema(
        description = "사용자의 학번",
        example = "1101",
    )
    @field:Min(1000)
    @field:Max(3999)
    val studentNumber: Int,
    @field:Schema(
        description = "사용자의 이름",
        example = "홍길동",
    )
    @field:Pattern(
        regexp = "^[가-힣]+$",
    )
    val name: String,
    @field:Schema(
        description = "비밀번호 재설정 OTP를 받을 이메일 주소",
        example = "student@xquare.app",
    )
    @field:Email
    val email: String,
)
