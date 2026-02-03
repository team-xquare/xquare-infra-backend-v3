package app.xquare.xquareinfra.application.team

import app.xquare.xquareinfra.application.global.exception.UseCaseException

sealed class UserException : UseCaseException() {
    data object UserNotFound : UserException()
}
