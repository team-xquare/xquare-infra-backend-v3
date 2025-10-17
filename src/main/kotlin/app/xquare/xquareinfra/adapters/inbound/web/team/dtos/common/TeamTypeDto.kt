package app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common

import app.xquare.xquareinfra.domain.team.TeamType
import com.fasterxml.jackson.annotation.JsonValue

enum class TeamTypeDto(
    @JsonValue val value: String,
) {
    CLUB("club"),
    TEAM("team"),
    INDIVIDUAL("individual"),
}

fun TeamType.toDto(): TeamTypeDto =
    when (this) {
        TeamType.CLUB -> TeamTypeDto.CLUB
        TeamType.TEAM -> TeamTypeDto.TEAM
        TeamType.INDIVIDUAL -> TeamTypeDto.INDIVIDUAL
    }

fun TeamTypeDto.toDomain(): TeamType =
    when (this) {
        TeamTypeDto.CLUB -> TeamType.CLUB
        TeamTypeDto.TEAM -> TeamType.TEAM
        TeamTypeDto.INDIVIDUAL -> TeamType.INDIVIDUAL
    }
