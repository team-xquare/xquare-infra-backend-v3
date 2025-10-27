package app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration

import app.xquare.xquareinfra.domain.application.BuildConfiguration

data class BuildConfigurationDto(
    val type: BuildConfigurationTypeDto,
    val version: String? = null,
    val buildCommand: String? = null,
    val startCommand: String? = null,
    val inputPath: String? = null,
    val outputPath: String? = null,
    val workingDirectory: String? = null,
)

fun BuildConfigurationDto.toDomain(): BuildConfiguration =
    BuildConfiguration(
        type = type.toDomain(),
        version = version,
        buildCommand = buildCommand,
        startCommand = startCommand,
        inputPath = inputPath,
        outputPath = outputPath,
        workingDirectory = workingDirectory,
    )

fun BuildConfiguration.toDto(): BuildConfigurationDto =
    BuildConfigurationDto(
        type = type.toDto(),
        version = version,
        buildCommand = buildCommand,
        startCommand = startCommand,
        inputPath = inputPath,
        outputPath = outputPath,
        workingDirectory = workingDirectory,
    )
