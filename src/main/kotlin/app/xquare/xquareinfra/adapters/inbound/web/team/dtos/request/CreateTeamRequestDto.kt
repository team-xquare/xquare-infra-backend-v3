package app.xquare.xquareinfra.adapters.inbound.web.team.dtos.request

import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.TeamMemberRoleDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.TeamTypeDto
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateTeamRequestDto(
    @field:Size(min = 3, max = 45)
    @field:Pattern(regexp = "^[a-z]+$")
    val name: String,
    val type: TeamTypeDto,
    val initialMembers: List<InitialMemberRequestDto>,
) {
    data class InitialMemberRequestDto(
        val id: Long,
        val role: TeamMemberRoleDto,
    )
}
