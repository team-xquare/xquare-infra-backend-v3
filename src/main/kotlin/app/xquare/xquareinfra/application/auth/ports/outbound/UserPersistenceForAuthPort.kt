package app.xquare.xquareinfra.application.auth.ports.outbound

import app.xquare.xquareinfra.domain.user.User

interface UserPersistenceForAuthPort {
    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

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

    fun findByUsername(username: String): User?
}
