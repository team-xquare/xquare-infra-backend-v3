package app.xquare.xquareinfra.application.addon.ports.inbound

data class DeleteAddonCommand(
    val userId: Long,
    val addonId: Long,
)

data object DeleteAddonResult

interface DeleteAddonUseCase {
    fun deleteAddon(command: DeleteAddonCommand): DeleteAddonResult
}
