package app.xquare.xquareinfra.infrastructure.web.exception

import app.xquare.xquareinfra.adapters.inbound.web.auth.errorCode.AuthExceptionMapper
import app.xquare.xquareinfra.adapters.inbound.web.common.errorCode.CommonExceptionMapper
import app.xquare.xquareinfra.adapters.inbound.web.team.errorCode.TeamExceptionMapper
import app.xquare.xquareinfra.application.auth.AuthException
import app.xquare.xquareinfra.application.global.exception.CommonException
import app.xquare.xquareinfra.application.global.exception.UseCaseException
import app.xquare.xquareinfra.application.team.TeamException
import app.xquare.xquareinfra.infrastructure.web.toWrappedDto
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(UseCaseException::class)
    fun handleUseCaseException(ex: UseCaseException): ResponseEntity<Any> = mapUseCaseExceptionToResponseEntity(ex)

    override fun handleExceptionInternal(
        ex: java.lang.Exception,
        body: Any?,
        headers: HttpHeaders,
        statusCode: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? =
        when {
            statusCode.is4xxClientError -> {
                ResponseEntity
                    .status(statusCode)
                    .body(
                        mapInternalExceptionToErrorCode(ex).toWrappedDto(),
                    )
            }

            statusCode.is5xxServerError -> {
                ResponseEntity.status(statusCode).build()
            }

            else -> {
                super.handleExceptionInternal(ex, body, headers, statusCode, request)
            }
        }

    private fun mapUseCaseExceptionToResponseEntity(ex: UseCaseException): ResponseEntity<Any> =
        when (ex) {
            is CommonException -> CommonExceptionMapper.toResponseEntity(ex)
            is AuthException -> AuthExceptionMapper.toResponseEntity(ex)
            is TeamException -> TeamExceptionMapper.toResponseEntity(ex)
            else -> throw RuntimeException()
        }

    private fun mapInternalExceptionToErrorCode(ex: Exception): GlobalErrorCode =
        when (ex) {
            is BindException, is TypeMismatchException -> GlobalErrorCode.VALIDATION_ERROR
            is HttpRequestMethodNotSupportedException -> GlobalErrorCode.METHOD_NOT_ALLOWED
            is NoHandlerFoundException -> GlobalErrorCode.NOT_FOUND
            else -> GlobalErrorCode.BAD_REQUEST
        }
}
