package app.xquare.xquareinfra.adapters.inbound.web.environmentVariable.errorCode

import app.xquare.xquareinfra.application.environmentVariable.EnvironmentVariableException
import app.xquare.xquareinfra.infrastructure.web.toWrappedDto
import org.springframework.http.ResponseEntity

object EnvironmentVariableExceptionMapper {
    fun toResponseEntity(ex: EnvironmentVariableException): ResponseEntity<Any> =
        when (ex) {
            is EnvironmentVariableException.VariableNotFound ->
                ResponseEntity.badRequest().body(EnvironmentVariableErrorCode.VARIABLE_NOT_FOUND.toWrappedDto())
        }
}
