package app.xquare.xquareinfra.application.application.ports.outbound

import app.xquare.xquareinfra.domain.application.ApplicationConfiguration

interface ApplicationConfigurationPublishPort {
    fun getPublishedConfiguration(
        teamName: String,
        applicationName: String,
    ): ApplicationConfiguration?

    fun publishApplicationConfiguration(
        teamName: String,
        applicationName: String,
        configuration: ApplicationConfiguration,
    )

    fun unpublishApplicationConfiguration(
        teamName: String,
        applicationName: String,
    )
}
