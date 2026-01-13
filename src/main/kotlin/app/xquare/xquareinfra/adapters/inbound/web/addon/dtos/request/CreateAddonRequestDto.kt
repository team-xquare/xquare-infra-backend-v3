package app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.request

import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common.AddonConfigurationDto
import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common.AddonTypeDto
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateAddonRequestDto(
    val teamId: Long,
    @field:Size(min = 3, max = 45)
    @field:Pattern(regexp = "^[a-z-]+$")
    val name: String,
    val type: AddonTypeDto,
    val storageGi: Int,
    val configuration: AddonConfigurationDto,
)
