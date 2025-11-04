package app.xquare.xquareinfra.application.addon.ports.inbound

data class UpdateAddonCommand(
    val userId: Long,
    val addonId: Long,
    val storageGi: Int,
)

data object UpdateAddonResult

interface UpdateAddonUseCase {
    fun updateAddon(command: UpdateAddonCommand): UpdateAddonResult
}
