package app.xquare.xquareinfra.adapters.inbound.web.auth.errorCode

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class AuthErrorCode(
    override val value: String,
) : ErrorCode {
    USERNAME_ALREADY_EXISTS("AUTH_USERNAME_ALREADY_EXISTS"),
    EMAIL_ALREADY_EXISTS("AUTH_EMAIL_ALREADY_EXISTS"),
    INVALID_USER_INFO("AUTH_INVALID_USER_INFO"),
    INVALID_CREDENTIALS("AUTH_INVALID_CREDENTIALS"),
    INVALID_REFRESH_TOKEN("AUTH_INVALID_REFRESH_TOKEN"),
    OTP_NOT_FOUND("AUTH_OTP_NOT_FOUND"),
    OTP_MISMATCH("AUTH_OTP_MISMATCH"),
    EMAIL_NOT_VERIFIED("AUTH_EMAIL_NOT_VERIFIED"),
}
