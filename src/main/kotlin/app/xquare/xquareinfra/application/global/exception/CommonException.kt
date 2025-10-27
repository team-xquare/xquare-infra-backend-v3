package app.xquare.xquareinfra.application.global.exception

sealed class CommonException : UseCaseException() {
    data object UnAuthorized : CommonException()

    data object UserNotFound : CommonException()

    data object TeamNotFound : CommonException()

    data object ApplicationNotFound : CommonException()

    data object NotTeamMember : CommonException()
}
