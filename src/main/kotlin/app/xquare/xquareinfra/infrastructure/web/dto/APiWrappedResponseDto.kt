package app.xquare.xquareinfra.infrastructure.web.dto

data class APiWrappedResponseDto<T>(
    val success: Boolean,
    val data: T,
) {
    companion object {
        fun <T> success(data: T) = APiWrappedResponseDto(success = true, data = data)

        fun error(errorCode: String) = APiWrappedResponseDto(success = false, data = ErrorResponseDto(errorCode))
    }
}
