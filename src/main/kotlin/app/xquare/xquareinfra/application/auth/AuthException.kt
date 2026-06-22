package app.xquare.xquareinfra.application.auth

import app.xquare.xquareinfra.application.global.exception.UseCaseException

sealed class AuthException : UseCaseException() {
    data object InvalidCredentials : AuthException()

    data object UsernameAlreadyExists : AuthException()

    data object EmailAlreadyExists : AuthException()

    data object InvalidUserInfo : AuthException()

    data object InvalidRefreshToken : AuthException()

    data object OtpNotFound : AuthException()

    data object OtpMismatch : AuthException()

    data object OtpRateLimitExceeded : AuthException()

    data object EmailNotVerified : AuthException()

    data object PasswordResetTokenNotFound : AuthException()
}
