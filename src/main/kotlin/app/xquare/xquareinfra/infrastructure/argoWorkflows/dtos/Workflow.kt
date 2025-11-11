package app.xquare.xquareinfra.infrastructure.argoWorkflows.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class WorkflowAnnotations(
    @JsonProperty("app.kubernetes.io/git-sha")
    val gitSha: String? = null,
)

data class WorkflowStatus(
    val phase: String,
    val startedAt: LocalDateTime,
    val finishedAt: LocalDateTime,
)

data class WorkflowMetadata(
    val annotations: WorkflowAnnotations,
)

data class Workflow(
    val metadata: WorkflowMetadata,
    val status: WorkflowStatus,
)
