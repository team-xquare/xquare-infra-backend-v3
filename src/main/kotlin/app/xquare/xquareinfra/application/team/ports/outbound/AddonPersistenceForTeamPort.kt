package app.xquare.xquareinfra.application.team.ports.outbound

import app.xquare.xquareinfra.domain.addon.Addon

interface AddonPersistenceForTeamPort {
    fun listByTeamId(teamId: Long): List<Addon>
}
