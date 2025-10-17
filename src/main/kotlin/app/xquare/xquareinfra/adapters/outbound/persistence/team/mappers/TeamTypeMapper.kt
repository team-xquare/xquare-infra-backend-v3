package app.xquare.xquareinfra.adapters.outbound.persistence.team.mappers

import app.xquare.xquareinfra.domain.team.TeamType
import app.xquare.xquareinfra.infrastructure.persistence.team.schema.TeamPersistenceType

fun TeamType.toPersistence(): TeamPersistenceType =
    when (this) {
        TeamType.CLUB -> TeamPersistenceType.CLUB
        TeamType.TEAM -> TeamPersistenceType.TEAM
        TeamType.INDIVIDUAL -> TeamPersistenceType.INDIVIDUAL
    }

fun TeamPersistenceType.toDomain(): TeamType =
    when (this) {
        TeamPersistenceType.CLUB -> TeamType.CLUB
        TeamPersistenceType.TEAM -> TeamType.TEAM
        TeamPersistenceType.INDIVIDUAL -> TeamType.INDIVIDUAL
    }
