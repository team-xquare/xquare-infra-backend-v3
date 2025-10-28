package app.xquare.xquareinfra.adapters.outbound.publish.mappers

import app.xquare.xquareinfra.domain.addon.AddonType
import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsAddonType

fun AddonType.toGitOps(): GitOpsAddonType =
    when (this) {
        AddonType.MYSQL -> GitOpsAddonType.MYSQL
        AddonType.POSTGRES -> GitOpsAddonType.POSTGRESQL
        AddonType.REDIS -> GitOpsAddonType.REDIS
        AddonType.MONGODB -> GitOpsAddonType.MONGODB
        AddonType.KAFKA -> GitOpsAddonType.KAFKA
        AddonType.RABBITMQ -> GitOpsAddonType.RABBITMQ
    }
