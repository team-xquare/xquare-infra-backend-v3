package app.xquare.xquareinfra.infrastructure.web.dto

interface SuccessResponseDto

fun <T : SuccessResponseDto> T.toWrappedDto(): APiWrappedResponseDto<T> = APiWrappedResponseDto.success(this)
