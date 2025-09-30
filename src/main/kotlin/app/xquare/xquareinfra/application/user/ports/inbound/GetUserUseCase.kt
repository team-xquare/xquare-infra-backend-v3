package app.xquare.xquareinfra.application.user.ports.inbound

import app.xquare.xquareinfra.domain.user.User

data class GetUserQuery(
    val userId: Long,
)

sealed class GetUserResult {
    data class Success(
        val user: User,
    ) : GetUserResult()

    data object UserNotExists : GetUserResult()
}

interface GetUserUseCase {
    fun getUser(query: GetUserQuery): GetUserResult
}
