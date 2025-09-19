package app.xquare.xquareinfra.application.auth.ports.outbound

import app.xquare.xquareinfra.domain.user.User

interface UserPersistencePort {
    fun existsByUsername(username: String): Boolean

    fun save(user: User): User

    fun findByUsername(username: String): User?
}
