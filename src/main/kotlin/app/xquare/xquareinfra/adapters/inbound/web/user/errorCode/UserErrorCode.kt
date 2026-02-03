package app.xquare.xquareinfra.adapters.inbound.web.user.errorCode

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class UserErrorCode(
    override val value: String,
) : ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND"),
}