package app.xquare.xquareinfra.adapters.inbound.web.team.dtos.request

import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.TeamMemberRoleDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.TeamTypeDto

data class CreateTeamRequestDto(
    val name: String,
    val type: TeamTypeDto,
    val initialMembers: List<InitialMemberRequestDto>,
) {
    data class InitialMemberRequestDto(
        val id: Long,
        val role: TeamMemberRoleDto,
    )
}
