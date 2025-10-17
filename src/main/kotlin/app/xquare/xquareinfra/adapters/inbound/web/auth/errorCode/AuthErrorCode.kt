package app.xquare.xquareinfra.adapters.inbound.web.auth.errorCode

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class AuthErrorCode(
    override val value: String,
) : ErrorCode {
    USERNAME_ALREADY_EXISTS("AUTH_USERNAME_ALREADY_EXISTS"),
    INVALID_CREDENTIALS("AUTH_INVALID_CREDENTIALS"),
    INVALID_REFRESH_TOKEN("AUTH_INVALID_REFRESH_TOKEN"),
}
