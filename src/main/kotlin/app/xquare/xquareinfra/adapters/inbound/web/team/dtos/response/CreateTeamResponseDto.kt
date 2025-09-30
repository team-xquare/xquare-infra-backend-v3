package app.xquare.xquareinfra.adapters.inbound.web.team.dtos.response

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class CreateTeamResponseDto(
    val id: Long,
) : SuccessResponseDto
