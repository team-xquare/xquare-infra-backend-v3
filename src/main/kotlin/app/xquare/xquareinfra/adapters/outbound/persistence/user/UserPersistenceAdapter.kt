package app.xquare.xquareinfra.adapters.outbound.persistence.user

import app.xquare.xquareinfra.adapters.outbound.persistence.user.mappers.toDomain
import app.xquare.xquareinfra.adapters.outbound.persistence.user.mappers.toPersistence
import app.xquare.xquareinfra.application.auth.ports.outbound.UserPersistenceForAuthPort
import app.xquare.xquareinfra.application.team.ports.outbound.UserPersistenceForTeamPort
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
    UserPersistenceForUserPort,
    UserPersistenceForTeamPort {
    override fun existsByUsername(username: String): Boolean = userRepository.existsByUsername(username)

    override fun save(user: User): User {
        val entity = user.toPersistence()
        val savedEntity = userRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findByUsername(username: String): User? = userRepository.findByUsername(username)?.toDomain()

    override fun findById(id: Long): User? = userRepository.findById(id).getOrNull()?.toDomain()
}
