package app.xquare.xquareinfra.application.addon.ports.inbound

import app.xquare.xquareinfra.domain.addon.AddonType

data class CreateAddonCommand(
    val userId: Long,
    val teamId: Long,
    val name: String,
    val type: AddonType,
    val storageGi: Int,
)

data class CreateAddonResult(
    val addonId: Long,
)

interface CreateAddonUseCase {
    fun createAddon(command: CreateAddonCommand): CreateAddonResult
}
