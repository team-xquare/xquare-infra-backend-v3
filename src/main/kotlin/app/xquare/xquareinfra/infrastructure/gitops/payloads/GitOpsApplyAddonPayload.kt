package app.xquare.xquareinfra.infrastructure.gitops.payloads

import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsAddon

data class GitOpsApplyAddonPayload(
    val path: String,
    val action: String = "apply",
    val spec: GitOpsAddon,
)
