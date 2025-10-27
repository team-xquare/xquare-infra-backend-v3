package app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration

import app.xquare.xquareinfra.domain.application.ApplicationConfiguration

data class ApplicationConfigurationDto(
    val tier: ApplicationTierDto,
    val github: ApplicationGithubConfigurationDto,
    val build: BuildConfigurationDto,
    val endpoints: List<ApplicationEndpointDto>,
)

fun ApplicationConfiguration.toDto(): ApplicationConfigurationDto =
    ApplicationConfigurationDto(
        tier = tier.toDto(),
        github = github.toDto(),
        build = build.toDto(),
        endpoints = endpoints.map { it.toDto() },
    )

fun ApplicationConfigurationDto.toDomain(): ApplicationConfiguration =
    ApplicationConfiguration(
        tier = tier.toDomain(),
        github = github.toDomain(),
        build = build.toDomain(),
        endpoints = endpoints.map { it.toDomain() },
    )
