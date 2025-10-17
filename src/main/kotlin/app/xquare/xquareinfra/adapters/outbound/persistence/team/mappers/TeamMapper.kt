package app.xquare.xquareinfra.adapters.outbound.persistence.team.mappers

import app.xquare.xquareinfra.domain.team.Team
import app.xquare.xquareinfra.infrastructure.persistence.team.schema.TeamPersistenceEntity

fun Team.toPersistence(): TeamPersistenceEntity =
    TeamPersistenceEntity(
        id = this.id,
        name = this.name,
        type = this.type.toPersistence(),
        members = this.members.map { it.toPersistence() }.toMutableList(),
    )

fun TeamPersistenceEntity.toDomain(): Team =
    Team(
        id = this.id,
        name = this.name,
        type = this.type.toDomain(),
        members = this.members.map { it.toDomain() },
    )
