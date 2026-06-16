package app.xquare.xquareinfra.adapters.outbound.persistence.user

import app.xquare.xquareinfra.adapters.outbound.persistence.user.mappers.toDomain
import app.xquare.xquareinfra.adapters.outbound.persistence.user.mappers.toPersistence
import app.xquare.xquareinfra.application.application.ports.outbound.UserPersistenceForApplicationPort
import app.xquare.xquareinfra.application.accountRecovery.ports.outbound.UserPersistenceForAccountRecoveryPort
import app.xquare.xquareinfra.application.auth.ports.outbound.UserPersistenceForAuthPort
import app.xquare.xquareinfra.application.notice.ports.outbound.UserPersistenceForNoticePort
import app.xquare.xquareinfra.application.team.ports.outbound.UserPersistenceForTeamPort
import app.xquare.xquareinfra.application.user.ports.outbound.UserPersistenceForUserPort
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.infrastructure.persistence.user.repositories.UserRepository
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class UserPersistenceAdapter(
    private val userRepository: UserRepository,
) : UserPersistenceForAuthPort,
    UserPersistenceForAccountRecoveryPort,
    UserPersistenceForUserPort,
    UserPersistenceForTeamPort,
    UserPersistenceForApplicationPort,
    UserPersistenceForNoticePort {
    override fun existsByUsername(username: String): Boolean = userRepository.existsByUsername(username)

    override fun existsByEmail(email: String): Boolean = userRepository.existsByEmail(email)

    override fun save(user: User): User {
        val entity = user.toPersistence()
        val savedEntity = userRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findByUsername(username: String): User? = userRepository.findByUsername(username)?.toDomain()

    override fun findByEmail(email: String): User? = userRepository.findByEmail(email)?.toDomain()

    override fun findById(id: Long): User? = userRepository.findById(id).getOrNull()?.toDomain()

    override fun listByNameContaining(name: String): List<User> = userRepository.findByNameContainingIgnoreCase(name).map { it.toDomain() }

    override fun listByEmailContaining(email: String): List<User> =
        userRepository.findByEmailContainingIgnoreCase(email).map { it.toDomain() }

    override fun findByStudentNumberAndNameAndEmail(
        studentNumber: Int,
        name: String,
        email: String,
    ): List<User> = userRepository.findByStudentNumberAndNameAndEmailIgnoreCase(studentNumber, name, email).map { it.toDomain() }

    override fun findByUsernameAndStudentNumberAndNameAndEmail(
        username: String,
        studentNumber: Int,
        name: String,
        email: String,
    ): User? =
        userRepository
            .findByUsernameAndStudentNumberAndNameAndEmailIgnoreCase(username, studentNumber, name, email)
            ?.toDomain()
}
