package app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.response

import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common.AddonTierDto
import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common.AddonTypeDto
import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class GetAddonResponseDto(
    val id: Long,
    val name: String,
    val type: AddonTypeDto,
    val tier: AddonTierDto,
    val storageGi: Int,
) : SuccessResponseDto
