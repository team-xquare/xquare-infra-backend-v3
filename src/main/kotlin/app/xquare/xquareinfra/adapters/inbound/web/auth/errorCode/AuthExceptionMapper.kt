package app.xquare.xquareinfra.adapters.inbound.web.auth.errorCode

import app.xquare.xquareinfra.application.auth.AuthException
import app.xquare.xquareinfra.infrastructure.web.toWrappedDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object AuthExceptionMapper {
    fun toResponseEntity(ex: AuthException): ResponseEntity<Any> =
        when (ex) {
            is AuthException.UsernameAlreadyExists ->
                ResponseEntity.badRequest().body(AuthErrorCode.USERNAME_ALREADY_EXISTS.toWrappedDto())

            is AuthException.EmailAlreadyExists ->
                ResponseEntity.badRequest().body(AuthErrorCode.EMAIL_ALREADY_EXISTS.toWrappedDto())

            is AuthException.InvalidUserInfo ->
                ResponseEntity.badRequest().body(AuthErrorCode.INVALID_USER_INFO.toWrappedDto())

            is AuthException.InvalidCredentials ->
                ResponseEntity.badRequest().body(AuthErrorCode.INVALID_CREDENTIALS.toWrappedDto())

            is AuthException.InvalidRefreshToken ->
                ResponseEntity.badRequest().body(AuthErrorCode.INVALID_REFRESH_TOKEN.toWrappedDto())

            is AuthException.OtpNotFound ->
                ResponseEntity.badRequest().body(AuthErrorCode.OTP_NOT_FOUND.toWrappedDto())

            is AuthException.OtpMismatch ->
                ResponseEntity.badRequest().body(AuthErrorCode.OTP_MISMATCH.toWrappedDto())

            is AuthException.OtpRateLimitExceeded ->
                ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(AuthErrorCode.OTP_RATE_LIMIT_EXCEEDED.toWrappedDto())

            is AuthException.EmailNotVerified ->
                ResponseEntity.badRequest().body(AuthErrorCode.EMAIL_NOT_VERIFIED.toWrappedDto())

            is AuthException.PasswordResetTokenNotFound ->
                ResponseEntity.badRequest().body(AuthErrorCode.PASSWORD_RESET_TOKEN_NOT_FOUND.toWrappedDto())
        }
}
