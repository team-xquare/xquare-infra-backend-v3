package app.xquare.xquareinfra.application.addon.ports.inbound

import app.xquare.xquareinfra.domain.addon.AddonConfiguration
import app.xquare.xquareinfra.domain.addon.AddonType
import app.xquare.xquareinfra.domain.user.User

data class CreateAddonCommand(
    val user: User,
    val teamId: Long,
    val name: String,
    val type: AddonType,
    val storageGi: Int,
    val configuration: AddonConfiguration,
)

data class CreateAddonResult(
    val addonId: Long,
)

interface CreateAddonUseCase {
    fun createAddon(command: CreateAddonCommand): CreateAddonResult
}
