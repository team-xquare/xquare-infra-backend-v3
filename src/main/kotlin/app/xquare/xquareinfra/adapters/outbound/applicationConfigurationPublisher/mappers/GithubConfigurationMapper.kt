package app.xquare.xquareinfra.adapters.outbound.applicationConfigurationPublisher.mappers

import app.xquare.xquareinfra.domain.application.ApplicationGithubConfiguration
import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsGithubInfo

fun ApplicationGithubConfiguration.toGitOps(): GitOpsGithubInfo =
    GitOpsGithubInfo(
        owner = owner,
        repo = repo,
        branch = branch,
        installationId = installationId,
        hash = hash,
        triggerPaths = triggerPaths?.ifEmpty { null },
    )

fun GitOpsGithubInfo.toDomain(): ApplicationGithubConfiguration =
    ApplicationGithubConfiguration(
        owner = owner,
        repo = repo,
        branch = branch,
        installationId = installationId,
        hash = hash,
        triggerPaths = triggerPaths,
    )
