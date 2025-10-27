package app.xquare.xquareinfra.adapters.outbound.applicationConfigurationPublisher

import app.xquare.xquareinfra.adapters.outbound.applicationConfigurationPublisher.mappers.toDomain
import app.xquare.xquareinfra.adapters.outbound.applicationConfigurationPublisher.mappers.toGitOps
import app.xquare.xquareinfra.application.application.ports.outbound.ApplicationConfigurationPublishPort
import app.xquare.xquareinfra.domain.application.ApplicationConfiguration
import app.xquare.xquareinfra.infrastructure.gitops.GitOpsClient
import org.springframework.stereotype.Component

@Component
class GitOpsApplicationConfigurationPublisherAdapter(
    private val gitOpsClient: GitOpsClient,
) : ApplicationConfigurationPublishPort {
    override fun getPublishedConfiguration(
        teamName: String,
        applicationName: String,
    ): ApplicationConfiguration? = gitOpsClient.getApplication(teamName, applicationName)?.toDomain()

    override fun publishApplicationConfiguration(
        teamName: String,
        applicationName: String,
        configuration: ApplicationConfiguration,
    ) {
        gitOpsClient.applyApplication(teamName, applicationName, configuration.toGitOps(applicationName))
    }

    override fun unpublishApplicationConfiguration(
        teamName: String,
        applicationName: String,
    ) = gitOpsClient.removeApplication(teamName, applicationName)
}
