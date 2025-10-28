package app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common

import app.xquare.xquareinfra.domain.addon.AddonType
import com.fasterxml.jackson.annotation.JsonValue

enum class AddonTypeDto(
    @JsonValue val value: String,
) {
    MYSQL("mysql"),
    POSTGRES("postgres"),
    REDIS("redis"),
    MONGODB("mongodb"),
    KAFKA("kafka"),
    RABBITMQ("rabbitmq"),
}

fun AddonTypeDto.toDomain(): AddonType =
    when (this) {
        AddonTypeDto.MYSQL -> AddonType.MYSQL
        AddonTypeDto.POSTGRES -> AddonType.POSTGRES
        AddonTypeDto.REDIS -> AddonType.REDIS
        AddonTypeDto.MONGODB -> AddonType.MONGODB
        AddonTypeDto.KAFKA -> AddonType.KAFKA
        AddonTypeDto.RABBITMQ -> AddonType.RABBITMQ
    }

fun AddonType.toDto(): AddonTypeDto =
    when (this) {
        AddonType.MYSQL -> AddonTypeDto.MYSQL
        AddonType.POSTGRES -> AddonTypeDto.POSTGRES
        AddonType.REDIS -> AddonTypeDto.REDIS
        AddonType.MONGODB -> AddonTypeDto.MONGODB
        AddonType.KAFKA -> AddonTypeDto.KAFKA
        AddonType.RABBITMQ -> AddonTypeDto.RABBITMQ
    }
