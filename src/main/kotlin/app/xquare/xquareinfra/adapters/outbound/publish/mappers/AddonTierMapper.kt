package app.xquare.xquareinfra.adapters.outbound.publish.mappers

import app.xquare.xquareinfra.domain.addon.AddonTier
import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsAddonTier

fun AddonTier.toGitOps(): GitOpsAddonTier =
    when (this) {
        AddonTier.NANO -> GitOpsAddonTier.NANO
        AddonTier.MICRO -> GitOpsAddonTier.MICRO
        AddonTier.SMALL -> GitOpsAddonTier.SMALL
        AddonTier.MEDIUM -> GitOpsAddonTier.MEDIUM
        AddonTier.LARGE -> GitOpsAddonTier.LARGE
    }
