package app.xquare.xquareinfra.adapters.outbound.persistence.addon.mappers

import app.xquare.xquareinfra.domain.addon.AddonConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

val objectMapper: ObjectMapper =
    jacksonObjectMapper()
        .registerKotlinModule()

fun AddonConfiguration?.toJsonOrNull(): String? = this?.let { objectMapper.writeValueAsString(it) }

fun String?.toAddonConfiguration(): AddonConfiguration =
    this?.let { objectMapper.readValue(it, AddonConfiguration::class.java) } ?: AddonConfiguration(
        bootstrap = null,
    )
