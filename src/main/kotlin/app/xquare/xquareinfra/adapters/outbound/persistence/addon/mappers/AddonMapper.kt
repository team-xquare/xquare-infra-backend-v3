package app.xquare.xquareinfra.adapters.outbound.persistence.addon.mappers

import app.xquare.xquareinfra.adapters.outbound.persistence.team.mappers.toDomain
import app.xquare.xquareinfra.adapters.outbound.persistence.team.mappers.toPersistence
import app.xquare.xquareinfra.domain.addon.Addon
import app.xquare.xquareinfra.domain.addon.AddonTier
import app.xquare.xquareinfra.domain.addon.AddonType
import app.xquare.xquareinfra.infrastructure.persistence.addon.schema.AddonPersistenceEntity

fun AddonPersistenceEntity.toDomain(): Addon =
    Addon(
        id = this.id,
        name = this.name,
        team = this.team.toDomain(),
        type = AddonType.valueOf(this.type), // string → enum
        tier = AddonTier.valueOf(this.tier.name), // enum → 도메인 enum
        storageGi = this.storageGi,
    )

fun Addon.toPersistence(): AddonPersistenceEntity =
    AddonPersistenceEntity(
        id = this.id,
        name = this.name,
        team = this.team.toPersistence(),
        type = this.type.name, // enum → string
        tier = this.tier.toPersistence(), // 도메인 enum → DB enum
        storageGi = this.storageGi,
    )
