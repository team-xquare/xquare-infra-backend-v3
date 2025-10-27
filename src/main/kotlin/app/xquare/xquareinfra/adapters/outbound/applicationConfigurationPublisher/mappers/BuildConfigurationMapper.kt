package app.xquare.xquareinfra.adapters.outbound.applicationConfigurationPublisher.mappers

import app.xquare.xquareinfra.domain.application.BuildConfiguration
import app.xquare.xquareinfra.domain.application.BuildConfigurationType
import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsBuild

fun BuildConfiguration.toGitOps(): GitOpsBuild =
    when (type) {
        BuildConfigurationType.GRADLE ->
            GitOpsBuild.Gradle(
                javaVersion = version!!,
                jarOutputPath = outputPath!!,
                buildCommand = buildCommand!!,
            )
        BuildConfigurationType.MAVEN ->
            GitOpsBuild.Maven(
                javaVersion = version!!,
                jarOutputPath = outputPath!!,
                buildCommand = buildCommand!!,
            )
        BuildConfigurationType.NODE_JS ->
            GitOpsBuild.Nodejs(
                nodeVersion = version!!,
                buildCommand = buildCommand!!,
                startCommand = startCommand!!,
            )
        BuildConfigurationType.REACT ->
            GitOpsBuild.React(
                nodeVersion = version!!,
                buildCommand = buildCommand!!,
                distPath = outputPath!!,
            )
        BuildConfigurationType.VITE ->
            GitOpsBuild.Vite(
                nodeVersion = version!!,
                buildCommand = buildCommand!!,
                distPath = outputPath!!,
            )
        BuildConfigurationType.VUE ->
            GitOpsBuild.Vue(
                nodeVersion = version!!,
                buildCommand = buildCommand!!,
                distPath = outputPath!!,
            )
        BuildConfigurationType.NEXT_JS ->
            GitOpsBuild.Nextjs(
                nodeVersion = version!!,
                buildCommand = buildCommand!!,
                startCommand = startCommand!!,
            )
        BuildConfigurationType.GO ->
            GitOpsBuild.Go(
                goVersion = version!!,
                buildCommand = buildCommand!!,
                binaryName = outputPath!!,
            )
        BuildConfigurationType.RUST ->
            GitOpsBuild.Rust(
                rustVersion = version!!,
                buildCommand = buildCommand!!,
                binaryName = outputPath!!,
            )
        BuildConfigurationType.DJANGO ->
            GitOpsBuild.Django(
                pythonVersion = version!!,
                buildCommand = buildCommand!!,
                startCommand = startCommand!!,
            )
        BuildConfigurationType.FLASK ->
            GitOpsBuild.Flask(
                pythonVersion = version!!,
                buildCommand = buildCommand!!,
                startCommand = startCommand!!,
            )
        BuildConfigurationType.DOCKER ->
            GitOpsBuild.Docker(
                dockerfilePath = inputPath!!,
                contextPath = workingDirectory!!,
            )
    }

fun GitOpsBuild.toDomain(): BuildConfiguration =
    when (this) {
        is GitOpsBuild.Gradle ->
            BuildConfiguration(
                type = BuildConfigurationType.GRADLE,
                version = javaVersion,
                buildCommand = buildCommand,
                outputPath = jarOutputPath,
            )
        is GitOpsBuild.Maven ->
            BuildConfiguration(
                type = BuildConfigurationType.MAVEN,
                version = javaVersion,
                buildCommand = buildCommand,
                outputPath = jarOutputPath,
            )
        is GitOpsBuild.Nodejs ->
            BuildConfiguration(
                type = BuildConfigurationType.NODE_JS,
                version = nodeVersion,
                buildCommand = buildCommand,
                startCommand = startCommand,
            )
        is GitOpsBuild.React ->
            BuildConfiguration(
                type = BuildConfigurationType.REACT,
                version = nodeVersion,
                buildCommand = buildCommand,
                outputPath = distPath,
            )
        is GitOpsBuild.Vite ->
            BuildConfiguration(
                type = BuildConfigurationType.VITE,
                version = nodeVersion,
                buildCommand = buildCommand,
                outputPath = distPath,
            )
        is GitOpsBuild.Vue ->
            BuildConfiguration(
                type = BuildConfigurationType.VUE,
                version = nodeVersion,
                buildCommand = buildCommand,
                outputPath = distPath,
            )
        is GitOpsBuild.Nextjs ->
            BuildConfiguration(
                type = BuildConfigurationType.NEXT_JS,
                version = nodeVersion,
                buildCommand = buildCommand,
                startCommand = startCommand,
            )
        is GitOpsBuild.Go ->
            BuildConfiguration(
                type = BuildConfigurationType.GO,
                version = goVersion,
                buildCommand = buildCommand,
                outputPath = binaryName,
            )
        is GitOpsBuild.Rust ->
            BuildConfiguration(
                type = BuildConfigurationType.RUST,
                version = rustVersion,
                buildCommand = buildCommand,
                outputPath = binaryName,
            )
        is GitOpsBuild.Django ->
            BuildConfiguration(
                type = BuildConfigurationType.DJANGO,
                version = pythonVersion,
                buildCommand = buildCommand,
                startCommand = startCommand,
            )
        is GitOpsBuild.Flask ->
            BuildConfiguration(
                type = BuildConfigurationType.FLASK,
                version = pythonVersion,
                buildCommand = buildCommand,
                startCommand = startCommand,
            )
        is GitOpsBuild.Docker ->
            BuildConfiguration(
                type = BuildConfigurationType.DOCKER,
                inputPath = dockerfilePath,
                workingDirectory = contextPath,
            )
    }
