package app.xquare.xquareinfra.adapters.inbound.web.team.dtos.response

import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.response.GetAddonResponseDto

data class GetTeamAddonsResponseDto(
    val addons: List<GetAddonResponseDto>,
)
