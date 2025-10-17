package app.xquare.xquareinfra.adapters.outbound.persistence.team.mappers

import app.xquare.xquareinfra.adapters.outbound.persistence.user.mappers.toDomain
import app.xquare.xquareinfra.adapters.outbound.persistence.user.mappers.toPersistence
import app.xquare.xquareinfra.domain.team.TeamMember
import app.xquare.xquareinfra.infrastructure.persistence.team.schema.TeamMemberPersistenceEntity

fun TeamMember.toPersistence(): TeamMemberPersistenceEntity =
    TeamMemberPersistenceEntity(
        user = this.user.toPersistence(),
        role = this.role.toPersistence(),
    )

fun TeamMemberPersistenceEntity.toDomain(): TeamMember =
    TeamMember(
        user = this.user.toDomain(),
        role = this.role.toDomain(),
    )
