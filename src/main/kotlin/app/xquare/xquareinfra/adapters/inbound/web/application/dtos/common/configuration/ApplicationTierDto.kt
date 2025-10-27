package app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration

import app.xquare.xquareinfra.domain.application.ApplicationTier
import com.fasterxml.jackson.annotation.JsonValue

enum class ApplicationTierDto(
    @JsonValue val value: String,
) {
    NANO("nano"),
    MICRO("micro"),
    SMALL("small"),
    MEDIUM("medium"),
    LARGE("large"),
}

fun ApplicationTier.toDto(): ApplicationTierDto =
    when (this) {
        ApplicationTier.NANO -> ApplicationTierDto.NANO
        ApplicationTier.MICRO -> ApplicationTierDto.MICRO
        ApplicationTier.SMALL -> ApplicationTierDto.SMALL
        ApplicationTier.MEDIUM -> ApplicationTierDto.MEDIUM
        ApplicationTier.LARGE -> ApplicationTierDto.LARGE
    }

fun ApplicationTierDto.toDomain(): ApplicationTier =
    when (this) {
        ApplicationTierDto.NANO -> ApplicationTier.NANO
        ApplicationTierDto.MICRO -> ApplicationTier.MICRO
        ApplicationTierDto.SMALL -> ApplicationTier.SMALL
        ApplicationTierDto.MEDIUM -> ApplicationTier.MEDIUM
        ApplicationTierDto.LARGE -> ApplicationTier.LARGE
    }
