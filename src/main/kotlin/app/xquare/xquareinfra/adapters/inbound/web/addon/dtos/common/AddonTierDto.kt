package app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common

import app.xquare.xquareinfra.domain.addon.AddonTier
import com.fasterxml.jackson.annotation.JsonValue

enum class AddonTierDto(
    @JsonValue val value: String,
) {
    NANO("nano"),
    MICRO("micro"),
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large"),
}

fun AddonTierDto.toDomain(): AddonTier =
    when (this) {
        AddonTierDto.NANO -> AddonTier.NANO
        AddonTierDto.MICRO -> AddonTier.MICRO
        AddonTierDto.SMALL -> AddonTier.SMALL
        AddonTierDto.MEDIUM -> AddonTier.MEDIUM
        AddonTierDto.LARGE -> AddonTier.LARGE
    }

fun AddonTier.toDto(): AddonTierDto =
    when (this) {
        AddonTier.NANO -> AddonTierDto.NANO
        AddonTier.MICRO -> AddonTierDto.MICRO
        AddonTier.SMALL -> AddonTierDto.SMALL
        AddonTier.MEDIUM -> AddonTierDto.MEDIUM
        AddonTier.LARGE -> AddonTierDto.LARGE
    }
