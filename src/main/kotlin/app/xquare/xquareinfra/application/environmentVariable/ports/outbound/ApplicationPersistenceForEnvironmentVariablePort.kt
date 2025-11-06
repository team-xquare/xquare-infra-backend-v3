package app.xquare.xquareinfra.application.environmentVariable.ports.outbound

import app.xquare.xquareinfra.domain.application.Application

interface ApplicationPersistenceForEnvironmentVariablePort {
    fun findById(applicationId: Long): Application?
}
