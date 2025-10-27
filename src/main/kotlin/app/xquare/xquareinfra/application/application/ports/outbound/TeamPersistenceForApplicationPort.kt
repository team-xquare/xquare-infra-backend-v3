package app.xquare.xquareinfra.application.application.ports.outbound

import app.xquare.xquareinfra.domain.team.Team

interface TeamPersistenceForApplicationPort {
    fun findById(teamId: Long): Team?
}
