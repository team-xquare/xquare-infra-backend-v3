package app.xquare.xquareinfra.application.application.ports.outbound

import app.xquare.xquareinfra.domain.user.User

interface UserPersistenceForApplicationPort {
    fun findById(id: Long): User?
}
