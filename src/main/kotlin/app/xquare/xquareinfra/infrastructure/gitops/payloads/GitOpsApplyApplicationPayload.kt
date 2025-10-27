package app.xquare.xquareinfra.infrastructure.gitops.payloads

import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsApplication

data class GitOpsApplyApplicationPayload(
    val path: String,
    val action: String = "apply",
    val spec: GitOpsApplication,
)
