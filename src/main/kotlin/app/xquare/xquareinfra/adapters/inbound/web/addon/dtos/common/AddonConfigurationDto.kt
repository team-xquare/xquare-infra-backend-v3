package app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common

import app.xquare.xquareinfra.domain.addon.AddonConfiguration

data class AddonConfigurationDto(
    val bootstrap: String?,
)

fun AddonConfigurationDto.toDomain(): AddonConfiguration =
    AddonConfiguration(
        bootstrap = bootstrap,
    )

fun AddonConfiguration.toDto(): AddonConfigurationDto =
    AddonConfigurationDto(
        bootstrap = bootstrap,
    )
