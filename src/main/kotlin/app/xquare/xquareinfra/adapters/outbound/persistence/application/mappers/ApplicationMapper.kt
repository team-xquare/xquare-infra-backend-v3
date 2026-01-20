package app.xquare.xquareinfra.adapters.outbound.persistence.application.mappers

import app.xquare.xquareinfra.adapters.outbound.persistence.team.mappers.toDomain
import app.xquare.xquareinfra.adapters.outbound.persistence.team.mappers.toPersistence
import app.xquare.xquareinfra.domain.application.Application
import app.xquare.xquareinfra.domain.application.ApplicationConfiguration
import app.xquare.xquareinfra.infrastructure.persistence.application.schema.ApplicationPersistenceEntity
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

private val objectMapper =
    jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

fun Application.toPersistence(): ApplicationPersistenceEntity {
    val versionedConfig =
        mapOf(
            "version" to 1,
            "config" to configuration,
        )

    val configurationJson = objectMapper.writeValueAsString(versionedConfig)

    return ApplicationPersistenceEntity(
        id = id,
        team = team.toPersistence(),
        name = name,
        status = status.toPersistence(),
        configuration = configurationJson,
    )
}

fun ApplicationPersistenceEntity.toDomain(): Application {
    val rootNode = objectMapper.readTree(configuration)
    // handle version changes later
    val configNode = rootNode["config"]
    val config = runCatching { objectMapper.readValue<ApplicationConfiguration>(configNode.toString()) }.getOrNull()

    return Application(
        id = id,
        name = name,
        team = team.toDomain(),
        status = status.toDomain(),
        configuration = config,
    )
}
