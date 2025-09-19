package app.xquare.xquareinfra.adapters.inbound.web.auth

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class AuthErrorCode(
    override val value: String,
) : ErrorCode {
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS"),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS"),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN"),
}
