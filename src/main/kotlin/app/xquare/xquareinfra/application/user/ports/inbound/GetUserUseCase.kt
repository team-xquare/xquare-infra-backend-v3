package app.xquare.xquareinfra.application.user.ports.inbound

import app.xquare.xquareinfra.domain.user.User

data class GetUserQuery(
    val userId: Long,
)

data class GetUserResult(
    val user: User,
)

interface GetUserUseCase {
    fun getUser(query: GetUserQuery): GetUserResult
}
