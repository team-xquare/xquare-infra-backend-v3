package app.xquare.xquareinfra.adapters.outbound.publish.mappers

import app.xquare.xquareinfra.domain.application.ApplicationEndpoint
import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsEndpoint

fun ApplicationEndpoint.toGitOps(): GitOpsEndpoint =
    GitOpsEndpoint(
        port = port,
        routes = routes.ifEmpty { null },
    )

fun GitOpsEndpoint.toDomain(): ApplicationEndpoint =
    ApplicationEndpoint(
        port = port,
        routes = routes ?: emptyList(),
    )
