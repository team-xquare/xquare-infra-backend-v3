package app.xquare.xquareinfra.application.addon.ports.inbound

import app.xquare.xquareinfra.domain.addon.Addon

data class GetAddonQuery(
    val userId: Long,
    val addonId: Long,
)

data class GetAddonResult(
    val addon: Addon,
)

interface GetAddonUseCase {
    fun getAddon(query: GetAddonQuery): GetAddonResult
}
