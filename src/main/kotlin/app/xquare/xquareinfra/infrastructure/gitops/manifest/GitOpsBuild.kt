package app.xquare.xquareinfra.infrastructure.gitops.manifest

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes(
    JsonSubTypes.Type(value = GitOpsBuild.Gradle::class, name = "gradle"),
    JsonSubTypes.Type(value = GitOpsBuild.Maven::class, name = "maven"),
    JsonSubTypes.Type(value = GitOpsBuild.Nodejs::class, name = "nodejs"),
    JsonSubTypes.Type(value = GitOpsBuild.React::class, name = "react"),
    JsonSubTypes.Type(value = GitOpsBuild.Vite::class, name = "vite"),
    JsonSubTypes.Type(value = GitOpsBuild.Vue::class, name = "vue"),
    JsonSubTypes.Type(value = GitOpsBuild.Nextjs::class, name = "nextjs"),
    JsonSubTypes.Type(value = GitOpsBuild.Go::class, name = "go"),
    JsonSubTypes.Type(value = GitOpsBuild.Rust::class, name = "rust"),
    JsonSubTypes.Type(value = GitOpsBuild.Django::class, name = "django"),
    JsonSubTypes.Type(value = GitOpsBuild.Flask::class, name = "flask"),
    JsonSubTypes.Type(value = GitOpsBuild.Docker::class, name = "docker"),
)
sealed class GitOpsBuild {
    @JsonTypeName("gradle")
    data class Gradle(
        val javaVersion: String,
        val jarOutputPath: String,
        val buildCommand: String,
    ) : GitOpsBuild()

    @JsonTypeName("maven")
    data class Maven(
        val javaVersion: String,
        val buildCommand: String,
        val jarOutputPath: String,
    ) : GitOpsBuild()

    @JsonTypeName("nodejs")
    data class Nodejs(
        val nodeVersion: String,
        val buildCommand: String,
        val startCommand: String,
    ) : GitOpsBuild()

    @JsonTypeName("react")
    data class React(
        val nodeVersion: String,
        val buildCommand: String,
        val distPath: String,
    ) : GitOpsBuild()

    @JsonTypeName("vite")
    data class Vite(
        val nodeVersion: String,
        val buildCommand: String,
        val distPath: String,
    ) : GitOpsBuild()

    @JsonTypeName("vue")
    data class Vue(
        val nodeVersion: String,
        val buildCommand: String,
        val distPath: String,
    ) : GitOpsBuild()

    @JsonTypeName("nextjs")
    data class Nextjs(
        val nodeVersion: String,
        val buildCommand: String,
        val startCommand: String,
    ) : GitOpsBuild()

    @JsonTypeName("go")
    data class Go(
        val goVersion: String,
        val buildCommand: String,
        val binaryName: String,
    ) : GitOpsBuild()

    @JsonTypeName("rust")
    data class Rust(
        val rustVersion: String,
        val buildCommand: String,
        val binaryName: String,
    ) : GitOpsBuild()

    @JsonTypeName("django")
    data class Django(
        val pythonVersion: String,
        val buildCommand: String,
        val startCommand: String,
    ) : GitOpsBuild()

    @JsonTypeName("flask")
    data class Flask(
        val pythonVersion: String,
        val buildCommand: String,
        val startCommand: String,
    ) : GitOpsBuild()

    @JsonTypeName("docker")
    data class Docker(
        val dockerfilePath: String,
        val contextPath: String,
    ) : GitOpsBuild()
}
