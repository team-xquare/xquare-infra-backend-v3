package app.xquare.xquareinfra.adapters.outbound.persistence.addon.mappers

import app.xquare.xquareinfra.domain.addon.AddonTier
import app.xquare.xquareinfra.infrastructure.persistence.addon.schema.AddonPersistenceTier

fun AddonTier.toPersistence(): AddonPersistenceTier =
    when (this) {
        AddonTier.NANO -> AddonPersistenceTier.NANO
        AddonTier.MICRO -> AddonPersistenceTier.MICRO
        AddonTier.SMALL -> AddonPersistenceTier.SMALL
        AddonTier.MEDIUM -> AddonPersistenceTier.MEDIUM
        AddonTier.LARGE -> AddonPersistenceTier.LARGE
    }

fun AddonPersistenceTier.toDomain(): AddonTier =
    when (this) {
        AddonPersistenceTier.NANO -> AddonTier.NANO
        AddonPersistenceTier.MICRO -> AddonTier.MICRO
        AddonPersistenceTier.SMALL -> AddonTier.SMALL
        AddonPersistenceTier.MEDIUM -> AddonTier.MEDIUM
        AddonPersistenceTier.LARGE -> AddonTier.LARGE
    }
