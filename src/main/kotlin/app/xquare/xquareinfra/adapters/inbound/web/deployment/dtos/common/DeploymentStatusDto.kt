package app.xquare.xquareinfra.adapters.inbound.web.deployment.dtos.common

import app.xquare.xquareinfra.domain.deployment.DeploymentStatus
import com.fasterxml.jackson.annotation.JsonValue

enum class DeploymentStatusDto(
    @JsonValue val value: String,
) {
    DEPLOYING("deploying"),
    SUCCEEDED("succeeded"),
    FAILED("failed"),
}

fun DeploymentStatus.toDto(): DeploymentStatusDto =
    when (this) {
        DeploymentStatus.DEPLOYING -> DeploymentStatusDto.DEPLOYING
        DeploymentStatus.SUCCEEDED -> DeploymentStatusDto.SUCCEEDED
        DeploymentStatus.FAILED -> DeploymentStatusDto.FAILED
    }
