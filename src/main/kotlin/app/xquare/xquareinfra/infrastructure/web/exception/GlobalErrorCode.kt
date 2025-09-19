package app.xquare.xquareinfra.infrastructure.web.exception

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class GlobalErrorCode(
    override val value: String,
) : ErrorCode {
    VALIDATION_ERROR("VALIDATION_ERROR"),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED"),
    BAD_REQUEST("BAD_REQUEST"),
    NOT_FOUND("NOT_FOUND"),
}
