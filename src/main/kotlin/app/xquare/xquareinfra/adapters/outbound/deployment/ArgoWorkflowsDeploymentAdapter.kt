package app.xquare.xquareinfra.adapters.outbound.deployment

import app.xquare.xquareinfra.adapters.outbound.deployment.mappers.toDomain
import app.xquare.xquareinfra.application.deployment.ports.outbound.DeploymentQueryPort
import app.xquare.xquareinfra.domain.application.Application
import app.xquare.xquareinfra.domain.deployment.Deployment
import app.xquare.xquareinfra.infrastructure.argoWorkflows.ArgoWorkflowsClient
import app.xquare.xquareinfra.infrastructure.argoWorkflows.ArgoWorkflowsProperties
import org.springframework.stereotype.Component

@Component
class ArgoWorkflowsDeploymentAdapter(
    private val argoWorkflowsClient: ArgoWorkflowsClient,
    private val argoWorkflowsProperties: ArgoWorkflowsProperties,
) : DeploymentQueryPort {
    override fun getDeployments(application: Application): List<Deployment> {
        val getWorkflowsResponse =
            argoWorkflowsClient.getWorkflows(
                "Bearer ${argoWorkflowsProperties.token}",
                namespace = teamNameToNamespace(application.team.name),
                labelSelector = "app.kubernetes.io/name=${application.name}",
            )

        return getWorkflowsResponse.body?.items.orEmpty().map {
            it.toDomain(application)
        }
    }

    private fun teamNameToNamespace(teamName: String): String = "$teamName-dsm-project"
}
