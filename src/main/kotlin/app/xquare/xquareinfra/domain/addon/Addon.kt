package app.xquare.xquareinfra.domain.addon

import app.xquare.xquareinfra.domain.team.Team

data class Addon(
    val id: Long? = null,
    val name: String,
    val team: Team,
    val type: AddonType,
    val storageGi: Int,
)
