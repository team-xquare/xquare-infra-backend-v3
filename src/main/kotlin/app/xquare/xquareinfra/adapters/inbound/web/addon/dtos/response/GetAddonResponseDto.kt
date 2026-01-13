package app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.response

import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common.AddonConfigurationDto
import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common.AddonTypeDto
import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class GetAddonResponseDto(
    val id: Long,
    val name: String,
    val type: AddonTypeDto,
    val storageGi: Int,
    val configuration: AddonConfigurationDto,
) : SuccessResponseDto
