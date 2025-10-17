package app.xquare.xquareinfra.application.global.exception

sealed class CommonException : UseCaseException() {
    data object UserNotFound : CommonException()

    data object TeamNotFound : CommonException()

    data object NotTeamMember : CommonException()
}
