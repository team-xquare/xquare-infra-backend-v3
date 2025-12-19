package app.xquare.xquareinfra.adapters.inbound.web.team.dtos.response

import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.TeamTypeDto
import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class GetTeamsResponseDto(
    val teams: List<GetTeamResponseDto>,
) : SuccessResponseDto
