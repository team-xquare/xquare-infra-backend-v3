package app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.request

import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common.AddonTypeDto

data class CreateAddonRequestDto(
    val teamId: Long,
    val name: String,
    val type: AddonTypeDto,
    val storageGi: Int,
)
