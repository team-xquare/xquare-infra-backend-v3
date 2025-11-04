package app.xquare.xquareinfra.application.addon

import app.xquare.xquareinfra.application.global.exception.UseCaseException

sealed class AddonException : UseCaseException() {
    data object AddonNameAlreadyExists : AddonException()
}
