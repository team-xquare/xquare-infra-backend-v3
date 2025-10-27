package app.xquare.xquareinfra.application.addon.ports.outbound

import app.xquare.xquareinfra.domain.team.Team

interface TeamPersistenceForAddonPort {
    fun findById(teamId: Long): Team?
}
