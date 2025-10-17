package app.xquare.xquareinfra.adapters.inbound.web.team.dtos.request

import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.TeamMemberRoleDto

data class AddOrUpdateMemberRequestDto(
    val id: Long,
    val role: TeamMemberRoleDto,
)

data class AddOrUpdateTeamMembersRequestDto(
    val members: List<AddOrUpdateMemberRequestDto>,
)
