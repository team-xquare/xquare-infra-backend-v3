package app.xquare.xquareinfra.infrastructure.web.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(hidden = true)
data class APiWrappedResponseDto<T>(
    val success: Boolean,
    val data: T? = null,
) {
    companion object {
        fun <T> success(data: T) = APiWrappedResponseDto(success = true, data = data)

        fun success() = APiWrappedResponseDto<Unit>(success = true)

        fun error(errorCode: String) = APiWrappedResponseDto(success = false, data = ErrorResponseDto(errorCode))
    }
}
