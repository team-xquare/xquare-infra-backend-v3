package app.xquare.xquareinfra.application.user.ports.outbound

import app.xquare.xquareinfra.domain.user.User

interface UserPersistenceForUserPort {
    fun findById(id: Long): User?

    fun listByNameContaining(name: String): List<User>

    fun listByEmailContaining(email: String): List<User>
}
