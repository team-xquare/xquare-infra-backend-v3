package app.xquare.xquareinfra.application.addon.ports.inbound

import app.xquare.xquareinfra.domain.user.User

data class DeleteAddonCommand(
    val user: User,
    val addonId: Long,
)

data object DeleteAddonResult

interface DeleteAddonUseCase {
    fun deleteAddon(command: DeleteAddonCommand): DeleteAddonResult
}
