package app.xquare.xquareinfra.application.environmentVariable

import app.xquare.xquareinfra.application.global.exception.UseCaseException

sealed class EnvironmentVariableException : UseCaseException() {
    data object VariableNotFound : EnvironmentVariableException()
}
