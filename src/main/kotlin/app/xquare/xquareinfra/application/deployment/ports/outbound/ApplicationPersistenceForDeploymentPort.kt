package app.xquare.xquareinfra.application.deployment.ports.outbound

import app.xquare.xquareinfra.domain.application.Application

interface ApplicationPersistenceForDeploymentPort {
    fun findById(applicationId: Long): Application?
}
