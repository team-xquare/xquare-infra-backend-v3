package app.xquare.xquareinfra.application.notice.ports.outbound

import app.xquare.xquareinfra.domain.user.User

interface UserPersistenceForNoticePort {
    fun findById(id: Long): User?
}
