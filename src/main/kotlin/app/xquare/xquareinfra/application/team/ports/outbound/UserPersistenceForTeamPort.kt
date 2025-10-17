package app.xquare.xquareinfra.application.team.ports.outbound

import app.xquare.xquareinfra.domain.user.User

interface UserPersistenceForTeamPort {
    fun findById(id: Long): User?
}
