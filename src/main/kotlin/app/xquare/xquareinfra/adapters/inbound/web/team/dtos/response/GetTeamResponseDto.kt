package app.xquare.xquareinfra.adapters.inbound.web.team.dtos.response

import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.TeamMemberRoleDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.TeamTypeDto
import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class GetTeamResponseDto(
    val id: Long,
    val name: String,
    val type: TeamTypeDto,
    val members: List<TeamMemberDto>,
) : SuccessResponseDto

data class TeamMemberDto(
    val userId: Long,
    val role: TeamMemberRoleDto,
)
