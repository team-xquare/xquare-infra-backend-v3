package app.xquare.xquareinfra.application.addon.ports.inbound

import app.xquare.xquareinfra.domain.addon.Addon
import app.xquare.xquareinfra.domain.user.User

data class GetAddonQuery(
    val user: User,
    val addonId: Long,
)

data class GetAddonResult(
    val addon: Addon,
)

interface GetAddonUseCase {
    fun getAddon(query: GetAddonQuery): GetAddonResult
}
