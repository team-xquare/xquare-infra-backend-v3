package app.xquare.xquareinfra.adapters.outbound.deployment.mappers

import app.xquare.xquareinfra.domain.application.Application
import app.xquare.xquareinfra.domain.deployment.Deployment
import app.xquare.xquareinfra.domain.deployment.DeploymentStatus
import app.xquare.xquareinfra.infrastructure.argoWorkflows.dtos.Workflow

private val phaseToStatusMap =
    mapOf(
        "" to DeploymentStatus.DEPLOYING,
        "Pending" to DeploymentStatus.DEPLOYING,
        "Running" to DeploymentStatus.DEPLOYING,
        "Succeeded" to DeploymentStatus.SUCCEEDED,
        "Failed" to DeploymentStatus.FAILED,
        "Error" to DeploymentStatus.FAILED,
    )

fun Workflow.toDomain(application: Application): Deployment =
    Deployment(
        application = application,
        commitHash = metadata.annotations.gitSha,
        startedAt = status.startedAt,
        finishedAt = status.finishedAt,
        status = phaseToStatusMap[status.phase] ?: DeploymentStatus.DEPLOYING,
    )
