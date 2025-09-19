package app.xquare.xquareinfra.infrastructure.persistence.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserPersistenceEntity, Long> {
    fun existsByUsername(username: String): Boolean

    fun findByUsername(username: String): UserPersistenceEntity?
}
