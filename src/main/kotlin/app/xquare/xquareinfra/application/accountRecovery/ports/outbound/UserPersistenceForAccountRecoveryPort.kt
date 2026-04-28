package app.xquare.xquareinfra.application.accountRecovery.ports.outbound

import app.xquare.xquareinfra.domain.user.User

interface UserPersistenceForAccountRecoveryPort {
    fun findByStudentNumberAndNameAndEmail(
        studentNumber: Int,
        name: String,
        email: String,
    ): List<User>
}
