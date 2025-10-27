package app.xquare.xquareinfra.adapters.outbound.applicationConfigurationPublisher.mappers

import app.xquare.xquareinfra.domain.application.ApplicationTier
import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsApplicationTier

fun ApplicationTier.toGitOps(): GitOpsApplicationTier =
    when (this) {
        ApplicationTier.NANO -> GitOpsApplicationTier.NANO
        ApplicationTier.MICRO -> GitOpsApplicationTier.MICRO
        ApplicationTier.SMALL -> GitOpsApplicationTier.SMALL
        ApplicationTier.MEDIUM -> GitOpsApplicationTier.MEDIUM
        ApplicationTier.LARGE -> GitOpsApplicationTier.LARGE
    }

fun GitOpsApplicationTier.toDomain(): ApplicationTier =
    when (this) {
        GitOpsApplicationTier.NANO -> ApplicationTier.NANO
        GitOpsApplicationTier.MICRO -> ApplicationTier.MICRO
        GitOpsApplicationTier.SMALL -> ApplicationTier.SMALL
        GitOpsApplicationTier.MEDIUM -> ApplicationTier.MEDIUM
        GitOpsApplicationTier.LARGE -> ApplicationTier.LARGE
    }
