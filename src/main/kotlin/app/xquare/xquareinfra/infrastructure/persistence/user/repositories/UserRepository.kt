package app.xquare.xquareinfra.infrastructure.persistence.user.repositories

import app.xquare.xquareinfra.infrastructure.persistence.user.schema.UserPersistenceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserPersistenceEntity, Long> {
    fun existsByUsername(username: String): Boolean

    fun existsByEmailIgnoreCase(email: String): Boolean

    fun findByUsername(username: String): UserPersistenceEntity?

    fun findByEmailIgnoreCase(email: String): UserPersistenceEntity?

    fun findByStudentNumberAndNameIgnoreCaseAndEmailIgnoreCase(
        studentNumber: Int,
        name: String,
        email: String,
    ): List<UserPersistenceEntity>

    fun findByUsernameAndStudentNumberAndNameIgnoreCaseAndEmailIgnoreCase(
        username: String,
        studentNumber: Int,
        name: String,
        email: String,
    ): UserPersistenceEntity?

    fun findByNameContainingIgnoreCase(name: String): List<UserPersistenceEntity>

    fun findByEmailContainingIgnoreCase(name: String): List<UserPersistenceEntity>
}
