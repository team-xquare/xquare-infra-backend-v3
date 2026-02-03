package app.xquare.xquareinfra.adapters.inbound.web.user.errorCode

import app.xquare.xquareinfra.application.team.UserException
import app.xquare.xquareinfra.infrastructure.web.toWrappedDto
import org.springframework.http.ResponseEntity

object UserExceptionMapper {
    fun toResponseEntity(ex: UserException): ResponseEntity<Any> =
        when (ex) {
            is UserException.UserNotFound ->
                ResponseEntity.badRequest().body(UserErrorCode.USER_NOT_FOUND.toWrappedDto())
        }
}
