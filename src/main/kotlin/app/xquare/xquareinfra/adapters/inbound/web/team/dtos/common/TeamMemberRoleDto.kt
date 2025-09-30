package app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common

import app.xquare.xquareinfra.domain.team.TeamMemberRole
import com.fasterxml.jackson.annotation.JsonValue

enum class TeamMemberRoleDto(
    @JsonValue val value: String,
) {
    ADMIN("admin"),
    CONTRIBUTOR("contributor"),
}

fun TeamMemberRole.toDto(): TeamMemberRoleDto =
    when (this) {
        TeamMemberRole.ADMIN -> TeamMemberRoleDto.ADMIN
        TeamMemberRole.CONTRIBUTOR -> TeamMemberRoleDto.CONTRIBUTOR
    }

fun TeamMemberRoleDto.toDomain(): TeamMemberRole =
    when (this) {
        TeamMemberRoleDto.ADMIN -> TeamMemberRole.ADMIN
        TeamMemberRoleDto.CONTRIBUTOR -> TeamMemberRole.CONTRIBUTOR
    }
