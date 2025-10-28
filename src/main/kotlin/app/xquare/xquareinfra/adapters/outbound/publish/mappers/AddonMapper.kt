package app.xquare.xquareinfra.adapters.outbound.publish.mappers

import app.xquare.xquareinfra.domain.addon.Addon
import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsAddon

fun Addon.toGitOps(): GitOpsAddon =
    GitOpsAddon(
        name = this.name,
        type = this.type.toGitOps(),
        tier = this.tier.toGitOps(),
        storage = "${this.storageGi}Gi",
    )
