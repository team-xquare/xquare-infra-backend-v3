package app.xquare.xquareinfra.application.user.ports.inbound

import app.xquare.xquareinfra.domain.user.User

data class SearchUsersQuery(
    val name: String?,
    val email: String?,
)

data class SearchUsersResult(
    val users: List<User>,
)

interface SearchUsersUseCase {
    fun searchUsers(query: SearchUsersQuery): SearchUsersResult
}
