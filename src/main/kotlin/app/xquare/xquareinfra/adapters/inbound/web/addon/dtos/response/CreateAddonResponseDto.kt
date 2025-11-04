package app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.response

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class CreateAddonResponseDto(
    val addonId: Long,
) : SuccessResponseDto
