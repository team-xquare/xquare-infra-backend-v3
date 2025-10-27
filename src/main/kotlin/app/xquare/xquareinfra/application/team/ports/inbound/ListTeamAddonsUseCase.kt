package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.addon.Addon

data class ListTeamAddonsQuery(
    val userId: Long,
    val teamId: Long,
)

data class ListTeamAddonsResult(
    val addons: List<Addon>,
)

interface ListTeamAddonsUseCase {
    fun listTeamAddons(query: ListTeamAddonsQuery): ListTeamAddonsResult
}
