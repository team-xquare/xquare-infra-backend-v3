package app.xquare.xquareinfra.application.application.ports.outbound

import app.xquare.xquareinfra.domain.application.Application

interface ApplicationPersistenceForApplicationPort {
    fun existsByTeamIdAndName(teamId: Long, name: String): Boolean

    fun findById(applicationId: Long): Application?

    fun save(application: Application): Application

    fun delete(applicationId: Long)
}
