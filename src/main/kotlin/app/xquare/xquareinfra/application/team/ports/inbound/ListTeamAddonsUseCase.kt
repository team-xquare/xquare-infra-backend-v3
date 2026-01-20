package app.xquare.xquareinfra.application.team.ports.inbound

import app.xquare.xquareinfra.domain.addon.Addon
import app.xquare.xquareinfra.domain.user.User

data class ListTeamAddonsQuery(
    val user: User,
    val teamId: Long,
)

data class ListTeamAddonsResult(
    val addons: List<Addon>,
)

interface ListTeamAddonsUseCase {
    fun listTeamAddons(query: ListTeamAddonsQuery): ListTeamAddonsResult
}
