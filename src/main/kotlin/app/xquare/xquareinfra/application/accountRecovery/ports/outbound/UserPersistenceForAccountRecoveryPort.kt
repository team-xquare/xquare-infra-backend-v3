package app.xquare.xquareinfra.application.accountRecovery.ports.outbound

import app.xquare.xquareinfra.domain.user.User

interface UserPersistenceForAccountRecoveryPort {
    fun save(user: User): User

    fun findByEmail(email: String): User?

    fun findByStudentNumberAndNameAndEmail(
        studentNumber: Int,
        name: String,
        email: String,
    ): List<User>

    fun findByUsernameAndStudentNumberAndNameAndEmail(
        username: String,
        studentNumber: Int,
        name: String,
        email: String,
    ): User?
}
