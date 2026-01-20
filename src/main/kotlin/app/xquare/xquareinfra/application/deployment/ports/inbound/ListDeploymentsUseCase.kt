package app.xquare.xquareinfra.application.deployment.ports.inbound

import app.xquare.xquareinfra.domain.deployment.Deployment
import app.xquare.xquareinfra.domain.user.User

data class ListDeploymentsQuery(
    val user: User,
    val applicationId: Long,
    val page: Int,
    val limit: Int,
)

data class ListDeploymentsResult(
    val deployments: List<Deployment>,
)

interface ListDeploymentsUseCase {
    fun listDeployments(query: ListDeploymentsQuery): ListDeploymentsResult
}
