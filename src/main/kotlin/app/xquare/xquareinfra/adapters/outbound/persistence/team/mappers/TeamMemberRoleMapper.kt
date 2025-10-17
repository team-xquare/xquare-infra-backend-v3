package app.xquare.xquareinfra.adapters.outbound.persistence.team.mappers

import app.xquare.xquareinfra.domain.team.TeamMemberRole
import app.xquare.xquareinfra.infrastructure.persistence.team.schema.TeamMemberPersistenceRole

fun TeamMemberRole.toPersistence(): TeamMemberPersistenceRole =
    when (this) {
        TeamMemberRole.ADMIN -> TeamMemberPersistenceRole.ADMIN
        TeamMemberRole.CONTRIBUTOR -> TeamMemberPersistenceRole.CONTRIBUTOR
    }

fun TeamMemberPersistenceRole.toDomain(): TeamMemberRole =
    when (this) {
        TeamMemberPersistenceRole.ADMIN -> TeamMemberRole.ADMIN
        TeamMemberPersistenceRole.CONTRIBUTOR -> TeamMemberRole.CONTRIBUTOR
    }
