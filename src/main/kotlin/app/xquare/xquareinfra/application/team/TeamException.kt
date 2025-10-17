package app.xquare.xquareinfra.application.team

import app.xquare.xquareinfra.application.global.exception.UseCaseException

sealed class TeamException : UseCaseException() {
    data object TeamNameAlreadyExists : TeamException()

    data object NotTeamAdmin : TeamException()
}
