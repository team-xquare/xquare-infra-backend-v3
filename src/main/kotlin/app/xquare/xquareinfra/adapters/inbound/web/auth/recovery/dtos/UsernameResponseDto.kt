package app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto
import io.swagger.v3.oas.annotations.media.Schema

data class UsernameResponseDto(
    @field:Schema(
        description = "아이디 찾기 OTP 검증에 성공한 사용자의 아이디",
        example = "xquare_user",
    )
    val username: String,
) : SuccessResponseDto
