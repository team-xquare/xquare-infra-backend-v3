package app.xquare.xquareinfra.infrastructure.gitops.manifest

data class GitOpsAddon(
    val name: String,
    val type: GitOpsAddonType,
    val storage: String,
)
