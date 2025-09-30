package app.xquare.xquareinfra.infrastructure.web

import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.ErrorResponseDto

interface ErrorCode {
    val value: String
}

fun <T : ErrorCode> T.toWrappedDto(): APiWrappedResponseDto<ErrorResponseDto> = APiWrappedResponseDto.error(this.value)
