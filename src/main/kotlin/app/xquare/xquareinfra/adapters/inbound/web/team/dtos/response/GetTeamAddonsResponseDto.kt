package app.xquare.xquareinfra.adapters.inbound.web.team.dtos.response

import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.response.GetAddonResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class GetTeamAddonsResponseDto(
    val addons: List<GetAddonResponseDto>,
) : SuccessResponseDto
