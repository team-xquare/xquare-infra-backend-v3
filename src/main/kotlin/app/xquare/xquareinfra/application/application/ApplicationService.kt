package app.xquare.xquareinfra.application.application

import app.xquare.xquareinfra.application.application.ports.inbound.CreateApplicationCommand
import app.xquare.xquareinfra.application.application.ports.inbound.CreateApplicationResult
import app.xquare.xquareinfra.application.application.ports.inbound.CreateApplicationUseCase
import app.xquare.xquareinfra.application.application.ports.inbound.DeleteApplicationCommand
import app.xquare.xquareinfra.application.application.ports.inbound.DeleteApplicationResult
import app.xquare.xquareinfra.application.application.ports.inbound.DeleteApplicationUseCase
import app.xquare.xquareinfra.application.application.ports.inbound.GetApplicationQuery
import app.xquare.xquareinfra.application.application.ports.inbound.GetApplicationResult
import app.xquare.xquareinfra.application.application.ports.inbound.GetApplicationUseCase
import app.xquare.xquareinfra.application.application.ports.inbound.UpdateApplicationConfigurationCommand
import app.xquare.xquareinfra.application.application.ports.inbound.UpdateApplicationConfigurationResult
import app.xquare.xquareinfra.application.application.ports.inbound.UpdateApplicationConfigurationUseCase
import app.xquare.xquareinfra.application.application.ports.inbound.UpdateApplicationStatusCommand
import app.xquare.xquareinfra.application.application.ports.inbound.UpdateApplicationStatusResult
import app.xquare.xquareinfra.application.application.ports.inbound.UpdateApplicationStatusUseCase
import app.xquare.xquareinfra.application.application.ports.outbound.ApplicationConfigurationPublishPort
import app.xquare.xquareinfra.application.application.ports.outbound.ApplicationPersistenceForApplicationPort
import app.xquare.xquareinfra.application.application.ports.outbound.TeamPersistenceForApplicationPort
import app.xquare.xquareinfra.application.application.ports.outbound.UserPersistenceForApplicationPort
import app.xquare.xquareinfra.application.global.exception.CommonException
import app.xquare.xquareinfra.domain.application.Application
import app.xquare.xquareinfra.domain.application.ApplicationStatus
import app.xquare.xquareinfra.domain.user.UserRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ApplicationService(
    private val userPersistencePort: UserPersistenceForApplicationPort,
    private val applicationPersistencePort: ApplicationPersistenceForApplicationPort,
    private val teamPersistencePort: TeamPersistenceForApplicationPort,
    private val configurationPublishPort: ApplicationConfigurationPublishPort,
    private val applicationConfigurationPublishPort: ApplicationConfigurationPublishPort,
) : CreateApplicationUseCase,
    GetApplicationUseCase,
    UpdateApplicationConfigurationUseCase,
    UpdateApplicationStatusUseCase,
    DeleteApplicationUseCase {
    override fun createApplication(command: CreateApplicationCommand): CreateApplicationResult {
        if (applicationPersistencePort.existsByName(command.name)) {
            throw ApplicationException.ApplicationNameAlreadyExists
        }

        val team = teamPersistencePort.findById(command.teamId) ?: throw CommonException.TeamNotFound

        val application =
            Application(
                team = team,
                name = command.name,
                status = ApplicationStatus.PENDING,
                configuration = command.configuration,
            )

        val savedApplication = applicationPersistencePort.save(application)

        return CreateApplicationResult(applicationId = savedApplication.id!!)
    }

    override fun getApplication(query: GetApplicationQuery): GetApplicationResult {
        val application =
            applicationPersistencePort.findById(query.applicationId)
                ?: throw CommonException.ApplicationNotFound

        if (!application.team.isMember(query.userId)) {
            throw CommonException.NotTeamMember
        }

        if (application.status == ApplicationStatus.UNPUBLISHED) {
            return GetApplicationResult(application)
        }

        val configuration =
            configurationPublishPort.getPublishedConfiguration(application.team.name, application.name)
                ?: throw ApplicationException.FailedToFetchConfiguration

        val publishedApplication = application.copy(configuration = configuration)
        return GetApplicationResult(publishedApplication)
    }

    override fun updateApplicationConfiguration(command: UpdateApplicationConfigurationCommand): UpdateApplicationConfigurationResult {
        val application =
            applicationPersistencePort.findById(command.applicationId)
                ?: throw CommonException.ApplicationNotFound

        if (!application.team.isMember(command.userId)) {
            throw CommonException.NotTeamMember
        }

        if (application.status == ApplicationStatus.UNPUBLISHED) {
            val updatedApplication = application.copy(configuration = command.configuration)
            applicationPersistencePort.save(updatedApplication)
        } else {
            configurationPublishPort.publishApplicationConfiguration(application.team.name, application.name, command.configuration)
        }

        return UpdateApplicationConfigurationResult
    }

    override fun updateApplicationStatus(command: UpdateApplicationStatusCommand): UpdateApplicationStatusResult {
        val user = userPersistencePort.findById(command.userId) ?: throw CommonException.UserNotFound
        if (user.role != UserRole.ADMIN) {
            throw CommonException.UnAuthorized
        }

        if (command.status == ApplicationStatus.PENDING) {
            throw ApplicationException.InvalidStatus
        }

        val application =
            applicationPersistencePort.findById(command.applicationId)
                ?: throw CommonException.ApplicationNotFound

        if (command.status == application.status) {
            throw ApplicationException.InvalidStatus
        }

        when (command.status) {
            ApplicationStatus.UNPUBLISHED -> {
                val publishedConfiguration =
                    configurationPublishPort.getPublishedConfiguration(application.team.name, application.name)
                        ?: throw ApplicationException.FailedToFetchConfiguration

                val updatedApplication = application.copy(configuration = publishedConfiguration, status = ApplicationStatus.UNPUBLISHED)
                applicationPersistencePort.save(updatedApplication)

                configurationPublishPort.unpublishApplicationConfiguration(application.team.name, application.name)
            }
            ApplicationStatus.PUBLISHED -> {
                configurationPublishPort.publishApplicationConfiguration(
                    application.team.name,
                    application.name,
                    application.configuration,
                )

                val updatedApplication = application.copy(status = ApplicationStatus.PUBLISHED)
                applicationPersistencePort.save(updatedApplication)
            }

            else -> {
                // impossible
            }
        }

        return UpdateApplicationStatusResult
    }

    override fun deleteApplication(command: DeleteApplicationCommand): DeleteApplicationResult {
        val application =
            applicationPersistencePort.findById(command.applicationId)
                ?: throw CommonException.ApplicationNotFound

        if (!application.team.isMember(command.userId)) {
            throw CommonException.NotTeamMember
        }

        applicationPersistencePort.delete(application.id!!)
        applicationConfigurationPublishPort.unpublishApplicationConfiguration(application.team.name, application.name)
        return DeleteApplicationResult
    }
}
