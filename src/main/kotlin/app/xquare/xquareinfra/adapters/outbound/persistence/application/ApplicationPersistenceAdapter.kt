package app.xquare.xquareinfra.adapters.outbound.persistence.application

import app.xquare.xquareinfra.adapters.outbound.persistence.application.mappers.toDomain
import app.xquare.xquareinfra.adapters.outbound.persistence.application.mappers.toPersistence
import app.xquare.xquareinfra.application.application.ports.outbound.ApplicationPersistenceForApplicationPort
import app.xquare.xquareinfra.application.deployment.ports.outbound.ApplicationPersistenceForDeploymentPort
import app.xquare.xquareinfra.application.environmentVariable.ports.outbound.ApplicationPersistenceForEnvironmentVariablePort
import app.xquare.xquareinfra.application.team.ports.outbound.ApplicationPersistenceForTeamPort
import app.xquare.xquareinfra.domain.application.Application
import app.xquare.xquareinfra.infrastructure.persistence.application.repositories.ApplicationRepository
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class ApplicationPersistenceAdapter(
    private val applicationRepository: ApplicationRepository,
) : ApplicationPersistenceForApplicationPort,
    ApplicationPersistenceForTeamPort,
    ApplicationPersistenceForEnvironmentVariablePort,
    ApplicationPersistenceForDeploymentPort {
    override fun existsByName(name: String): Boolean = applicationRepository.existsByName(name)

    override fun findById(applicationId: Long): Application? = applicationRepository.findById(applicationId).getOrNull()?.toDomain()

    override fun save(application: Application): Application {
        val entity = application.toPersistence()
        val saved = applicationRepository.save(entity)
        return saved.toDomain()
    }

    override fun delete(applicationId: Long) = applicationRepository.deleteById(applicationId)

    override fun deleteByTeamId(teamId: Long) = applicationRepository.deleteAllByTeamId(teamId)

    override fun listByTeamId(teamId: Long): List<Application> = applicationRepository.findAllByTeamId(teamId).map { it.toDomain() }
}
