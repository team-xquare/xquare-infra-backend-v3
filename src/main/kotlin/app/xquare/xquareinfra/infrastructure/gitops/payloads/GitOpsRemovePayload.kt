package app.xquare.xquareinfra.infrastructure.gitops.payloads

data class GitOpsRemovePayload(
    val path: String,
    val action: String = "remove",
)
