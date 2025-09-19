package app.xquare.xquareinfra.infrastructure.web.exception

import app.xquare.xquareinfra.infrastructure.web.toWrappedDto
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {
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
                        mapToErrorCode(ex).toWrappedDto().also {
                            println(ex)
                        },
                    )
            }

            statusCode.is5xxServerError -> {
                ResponseEntity.status(statusCode).build()
            }

            else -> {
                super.handleExceptionInternal(ex, body, headers, statusCode, request)
            }
        }

    private fun mapToErrorCode(ex: Exception): GlobalErrorCode =
        when (ex) {
            is BindException, is TypeMismatchException -> GlobalErrorCode.VALIDATION_ERROR
            is HttpRequestMethodNotSupportedException -> GlobalErrorCode.METHOD_NOT_ALLOWED
            is NoHandlerFoundException -> GlobalErrorCode.NOT_FOUND
            else -> GlobalErrorCode.BAD_REQUEST
        }
}
