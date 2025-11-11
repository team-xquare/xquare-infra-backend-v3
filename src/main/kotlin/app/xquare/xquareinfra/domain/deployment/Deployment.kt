package app.xquare.xquareinfra.domain.deployment

import app.xquare.xquareinfra.domain.application.Application
import java.time.LocalDateTime

data class Deployment(
    val application: Application,
    val commitHash: String?,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
    val status: DeploymentStatus,
)
