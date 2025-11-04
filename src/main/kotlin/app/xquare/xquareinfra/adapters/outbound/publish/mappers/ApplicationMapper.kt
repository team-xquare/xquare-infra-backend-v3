package app.xquare.xquareinfra.adapters.outbound.publish.mappers

import app.xquare.xquareinfra.domain.application.ApplicationConfiguration
import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsApplication

fun ApplicationConfiguration.toGitOps(name: String): GitOpsApplication =
    GitOpsApplication(
        name = name,
        tier = this.tier.toGitOps(),
        github = this.github.toGitOps(),
        build = this.build.toGitOps(),
        endpoints = this.endpoints.ifEmpty { null }?.map { it.toGitOps() },
    )

fun GitOpsApplication.toDomain(): ApplicationConfiguration =
    ApplicationConfiguration(
        tier = this.tier.toDomain(),
        github = this.github.toDomain(),
        build = this.build.toDomain(),
        endpoints = this.endpoints?.map { it.toDomain() } ?: emptyList(),
    )
