package app.xquare.xquareinfra.application.addon.ports.outbound

import app.xquare.xquareinfra.domain.addon.Addon

interface AddonPublishPort {
    fun publishAddon(
        teamName: String,
        addon: Addon,
    )

    fun unpublishAddon(
        teamName: String,
        addon: Addon,
    )
}
