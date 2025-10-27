package app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration

import app.xquare.xquareinfra.domain.application.BuildConfigurationType
import com.fasterxml.jackson.annotation.JsonValue

enum class BuildConfigurationTypeDto(
    @JsonValue val value: String,
) {
    GRADLE("gradle"),
    NODE_JS("node_js"),
    REACT("react"),
    VITE("vite"),
    VUE("vue"),
    NEXT_JS("next_js"),
    GO("go"),
    RUST("rust"),
    MAVEN("maven"),
    DJANGO("django"),
    FLASK("flask"),
    DOCKER("docker"),
}

fun BuildConfigurationTypeDto.toDomain(): BuildConfigurationType =
    when (this) {
        BuildConfigurationTypeDto.GRADLE -> BuildConfigurationType.GRADLE
        BuildConfigurationTypeDto.NODE_JS -> BuildConfigurationType.NODE_JS
        BuildConfigurationTypeDto.REACT -> BuildConfigurationType.REACT
        BuildConfigurationTypeDto.VITE -> BuildConfigurationType.VITE
        BuildConfigurationTypeDto.VUE -> BuildConfigurationType.VUE
        BuildConfigurationTypeDto.NEXT_JS -> BuildConfigurationType.NEXT_JS
        BuildConfigurationTypeDto.GO -> BuildConfigurationType.GO
        BuildConfigurationTypeDto.RUST -> BuildConfigurationType.RUST
        BuildConfigurationTypeDto.MAVEN -> BuildConfigurationType.MAVEN
        BuildConfigurationTypeDto.DJANGO -> BuildConfigurationType.DJANGO
        BuildConfigurationTypeDto.FLASK -> BuildConfigurationType.FLASK
        BuildConfigurationTypeDto.DOCKER -> BuildConfigurationType.DOCKER
    }

fun BuildConfigurationType.toDto(): BuildConfigurationTypeDto =
    when (this) {
        BuildConfigurationType.GRADLE -> BuildConfigurationTypeDto.GRADLE
        BuildConfigurationType.NODE_JS -> BuildConfigurationTypeDto.NODE_JS
        BuildConfigurationType.REACT -> BuildConfigurationTypeDto.REACT
        BuildConfigurationType.VITE -> BuildConfigurationTypeDto.VITE
        BuildConfigurationType.VUE -> BuildConfigurationTypeDto.VUE
        BuildConfigurationType.NEXT_JS -> BuildConfigurationTypeDto.NEXT_JS
        BuildConfigurationType.GO -> BuildConfigurationTypeDto.GO
        BuildConfigurationType.RUST -> BuildConfigurationTypeDto.RUST
        BuildConfigurationType.MAVEN -> BuildConfigurationTypeDto.MAVEN
        BuildConfigurationType.DJANGO -> BuildConfigurationTypeDto.DJANGO
        BuildConfigurationType.FLASK -> BuildConfigurationTypeDto.FLASK
        BuildConfigurationType.DOCKER -> BuildConfigurationTypeDto.DOCKER
    }
