package app.xquare.xquareinfra.adapters.inbound.web.application.errorCode

import app.xquare.xquareinfra.application.application.ApplicationException
import app.xquare.xquareinfra.infrastructure.web.toWrappedDto
import org.springframework.http.ResponseEntity

object ApplicationExceptionMapper {
    fun toResponseEntity(ex: ApplicationException): ResponseEntity<Any> =
        when (ex) {
            is ApplicationException.ApplicationNameAlreadyExists ->
                ResponseEntity.badRequest().body(ApplicationErrorCode.APPLICATION_ALREADY_EXISTS.toWrappedDto())
            is ApplicationException.FailedToFetchConfiguration ->
                ResponseEntity.internalServerError().body(ApplicationErrorCode.CONFIGURATION_FETCH_FAILED.toWrappedDto())
            is ApplicationException.InvalidStatus ->
                ResponseEntity.badRequest().body(ApplicationErrorCode.INVALID_STATUS.toWrappedDto())
        }
}
