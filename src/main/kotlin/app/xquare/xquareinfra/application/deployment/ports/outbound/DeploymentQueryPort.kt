package app.xquare.xquareinfra.application.deployment.ports.outbound

import app.xquare.xquareinfra.domain.application.Application
import app.xquare.xquareinfra.domain.deployment.Deployment

interface DeploymentQueryPort {
    fun getDeployments(application: Application): List<Deployment>
}
