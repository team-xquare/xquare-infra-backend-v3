package app.xquare.xquareinfra.application.addon.ports.inbound

import app.xquare.xquareinfra.domain.user.User

data class UpdateAddonCommand(
    val user: User,
    val addonId: Long,
    val storageGi: Int,
)

data object UpdateAddonResult

interface UpdateAddonUseCase {
    fun updateAddon(command: UpdateAddonCommand): UpdateAddonResult
}
