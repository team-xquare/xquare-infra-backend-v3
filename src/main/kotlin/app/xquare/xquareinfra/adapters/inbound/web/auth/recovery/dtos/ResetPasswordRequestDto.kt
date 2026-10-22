package app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ResetPasswordRequestDto(
    @field:Schema(
        description = "비밀번호 재설정 OTP 검증 후 발급받은 재설정 토큰",
        example = "7f4f7c50-4b53-4b63-a76d-625deea5b61f",
    )
    @field:NotBlank
    val passwordResetToken: String,
    @field:Schema(
        description = "새 비밀번호. 8자 이상 20자 이하이며 특수문자를 1개 이상 포함해야 합니다.",
        example = "newPassword!1",
    )
    @field:Size(min = 8, max = 20)
    @field:Pattern(
        regexp = ".*[!@#\$%^&*(),.?\":{}|<>].*",
    )
    val newPassword: String,
)
