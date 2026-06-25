package app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SendUsernameFindOtpRequestDto(
    @field:Schema(
        description = "사용자의 학번",
        example = "1101",
    )
    @field:Min(1000)
    @field:Max(3999)
    val studentNumber: Int,
    @field:Schema(
        description = "사용자의 이름. 한글 또는 영문 이름이며 공백과 하이픈을 포함할 수 있습니다.",
        example = "홍길동",
    )
    @field:Pattern(
        regexp = "^[A-Za-z가-힣]+(?:[ -][A-Za-z가-힣]+)*$",
    )
    @field:NotBlank
    val name: String,
    @field:Schema(
        description = "아이디 찾기 OTP를 받을 이메일 주소",
        example = "student@xquare.app",
    )
    @field:Email
    @field:NotBlank
    val email: String,
)
