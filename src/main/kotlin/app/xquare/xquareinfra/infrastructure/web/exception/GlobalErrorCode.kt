package app.xquare.xquareinfra.infrastructure.web.exception

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class GlobalErrorCode(
    override val value: String,
) : ErrorCode {
    VALIDATION_ERROR("GLOBAL_VALIDATION_ERROR"),
    METHOD_NOT_ALLOWED("GLOBAL_METHOD_NOT_ALLOWED"),
    BAD_REQUEST("GLOBAL_BAD_REQUEST"),
    NOT_FOUND("GLOBAL_NOT_FOUND"),
}
