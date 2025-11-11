package app.xquare.xquareinfra.application.deployment

import app.xquare.xquareinfra.application.deployment.ports.inbound.ListDeploymentsQuery
import app.xquare.xquareinfra.application.deployment.ports.inbound.ListDeploymentsResult
import app.xquare.xquareinfra.application.deployment.ports.inbound.ListDeploymentsUseCase
import app.xquare.xquareinfra.application.deployment.ports.outbound.ApplicationPersistenceForDeploymentPort
import app.xquare.xquareinfra.application.deployment.ports.outbound.DeploymentQueryPort
import app.xquare.xquareinfra.application.global.exception.CommonException
import org.springframework.stereotype.Service

@Service
class DeploymentService(
    private val deploymentQueryPort: DeploymentQueryPort,
    private val applicationPersistencePort: ApplicationPersistenceForDeploymentPort,
) : ListDeploymentsUseCase {
    override fun listDeployments(query: ListDeploymentsQuery): ListDeploymentsResult {
        val application =
            applicationPersistencePort.findById(query.applicationId)
                ?: throw CommonException.ApplicationNotFound

        if (!application.team.isMember(query.userId)) {
            throw CommonException.NotTeamMember
        }

        val deployments = deploymentQueryPort.getDeployments(application)
        return ListDeploymentsResult(deployments)
    }
}
