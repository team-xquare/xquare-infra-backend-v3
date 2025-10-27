package app.xquare.xquareinfra.application.team.ports.outbound

import app.xquare.xquareinfra.domain.application.Application

interface ApplicationPersistenceForTeamPort {
    fun deleteByTeamId(teamId: Long)

    fun listByTeamId(teamId: Long): List<Application>
}
