package app.xquare.xquareinfra.adapters.outbound.persistence.user

import app.xquare.xquareinfra.application.auth.ports.outbound.UserPersistenceForAuthPort
import app.xquare.xquareinfra.application.user.ports.outbound.UserPersistenceForUserPort
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.domain.user.UserRole
import app.xquare.xquareinfra.infrastructure.persistence.user.repositories.UserRepository
import app.xquare.xquareinfra.infrastructure.persistence.user.schema.UserPersistenceEntity
import app.xquare.xquareinfra.infrastructure.persistence.user.schema.UserPersistenceRole
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class UserPersistenceAdapter(
    private val userRepository: UserRepository,
) : UserPersistenceForAuthPort,
    UserPersistenceForUserPort {
    override fun existsByUsername(username: String): Boolean = userRepository.existsByUsername(username)

    override fun save(user: User): User {
        val entity = user.toPersistence()
        val savedEntity = userRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findByUsername(username: String): User? = userRepository.findByUsername(username)?.toDomain()

    override fun findById(id: Long): User? = userRepository.findById(id).getOrNull()?.toDomain()

    private fun UserRole.toPersistence(): UserPersistenceRole =
        when (this) {
            UserRole.ADMIN -> UserPersistenceRole.ADMIN
            UserRole.MEMBER -> UserPersistenceRole.MEMBER
        }

    private fun UserPersistenceRole.toDomain(): UserRole =
        when (this) {
            UserPersistenceRole.ADMIN -> UserRole.ADMIN
            UserPersistenceRole.MEMBER -> UserRole.MEMBER
        }

    fun User.toPersistence(): UserPersistenceEntity =
        UserPersistenceEntity(
            id = this.id,
            username = this.username,
            password = this.password,
            role = this.role.toPersistence(),
            studentNumber = this.studentNumber,
            name = this.name,
            email = this.email,
        )

    private fun UserPersistenceEntity.toDomain(): User =
        User(
            id = this.id,
            username = this.username,
            password = this.password,
            role = this.role.toDomain(),
            studentNumber = this.studentNumber,
            name = this.name,
            email = this.email,
        )
}
