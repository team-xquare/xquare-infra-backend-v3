package app.xquare.xquareinfra.adapters.inbound.web.deployment.dtos.response

import app.xquare.xquareinfra.adapters.inbound.web.deployment.dtos.common.DeploymentStatusDto
import java.time.LocalDateTime

data class DeploymentDto(
    val applicationId: Long,
    val commitHash: String?,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
    val status: DeploymentStatusDto,
)

data class GetDeploymentsResponseDto(
    val deployments: List<DeploymentDto>,
)
