package app.xquare.xquareinfra.application.application

import app.xquare.xquareinfra.application.global.exception.UseCaseException

sealed class ApplicationException : UseCaseException() {
    data object ApplicationNameAlreadyExists : ApplicationException()

    data object FailedToFetchConfiguration : ApplicationException()

    data object InvalidStatus : ApplicationException()
}
