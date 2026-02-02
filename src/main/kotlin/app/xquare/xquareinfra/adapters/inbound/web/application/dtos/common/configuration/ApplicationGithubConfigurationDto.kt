package app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration

import app.xquare.xquareinfra.domain.application.ApplicationGithubConfiguration

data class ApplicationGithubConfigurationDto(
    val owner: String,
    val repo: String,
    val branch: String,
    val installationId: String,
    val hash: String?,
    val triggerPaths: List<String>? = null,
)

fun ApplicationGithubConfigurationDto.toDomain() =
    ApplicationGithubConfiguration(
        owner = owner,
        repo = repo,
        branch = branch,
        installationId = installationId,
        hash = hash,
        triggerPaths = triggerPaths,
    )

fun ApplicationGithubConfiguration.toDto() =
    ApplicationGithubConfigurationDto(
        owner = owner,
        repo = repo,
        branch = branch,
        installationId = installationId,
        hash = hash,
        triggerPaths = triggerPaths,
    )
