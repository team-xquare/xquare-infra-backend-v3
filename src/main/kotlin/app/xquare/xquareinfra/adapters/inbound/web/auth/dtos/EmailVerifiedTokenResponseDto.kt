package app.xquare.xquareinfra.adapters.inbound.web.auth.dtos

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto
import io.swagger.v3.oas.annotations.media.Schema

data class EmailVerifiedTokenResponseDto(
    @field:Schema(
        description = "회원가입 요청에 함께 제출해야 하는 이메일 인증 토큰",
        example = "7f4f7c50-4b53-4b63-a76d-625deea5b61f",
    )
    val emailVerifiedToken: String,
) : SuccessResponseDto
