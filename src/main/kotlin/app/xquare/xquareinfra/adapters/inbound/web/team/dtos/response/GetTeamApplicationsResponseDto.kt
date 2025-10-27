package app.xquare.xquareinfra.adapters.inbound.web.team.dtos.response

import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.response.GetApplicationResponseDto

data class GetTeamApplicationsResponseDto(
    val applications: List<GetApplicationResponseDto>,
)
