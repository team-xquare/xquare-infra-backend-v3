package app.xquare.xquareinfra.adapters.inbound.web.team.dtos.request

import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.TeamTypeDto

data class UpdateTeamRequestDto(
    val type: TeamTypeDto?,
)
