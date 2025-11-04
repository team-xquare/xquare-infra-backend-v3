package app.xquare.xquareinfra.adapters.outbound.publish

import app.xquare.xquareinfra.adapters.outbound.publish.mappers.toDomain
import app.xquare.xquareinfra.adapters.outbound.publish.mappers.toGitOps
import app.xquare.xquareinfra.application.addon.ports.outbound.AddonPublishPort
import app.xquare.xquareinfra.application.application.ports.outbound.ApplicationConfigurationPublishPort
import app.xquare.xquareinfra.domain.addon.Addon
import app.xquare.xquareinfra.domain.application.ApplicationConfiguration
import app.xquare.xquareinfra.infrastructure.gitops.GitOpsClient
import org.springframework.stereotype.Component

@Component
class GitOpsPublisherAdapter(
    private val gitOpsClient: GitOpsClient,
) : ApplicationConfigurationPublishPort,
    AddonPublishPort {
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

    override fun publishAddon(
        teamName: String,
        addon: Addon,
    ) = gitOpsClient.applyAddon(teamName, addon.name, addon.toGitOps())

    override fun unpublishAddon(
        teamName: String,
        addon: Addon,
    ) = gitOpsClient.removeAddon(teamName, addon.name)
}
