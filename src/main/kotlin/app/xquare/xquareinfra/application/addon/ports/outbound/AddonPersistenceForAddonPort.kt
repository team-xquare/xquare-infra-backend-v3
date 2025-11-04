package app.xquare.xquareinfra.application.addon.ports.outbound

import app.xquare.xquareinfra.domain.addon.Addon

interface AddonPersistenceForAddonPort {
    fun findById(addonId: Long): Addon?

    fun existsByNameAndTeamId(
        name: String,
        teamId: Long,
    ): Boolean

    fun save(addon: Addon): Addon

    fun delete(addonId: Long)
}
