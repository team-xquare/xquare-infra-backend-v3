package app.xquare.xquareinfra.infrastructure.argoWorkflows

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "argo-workflows")
data class ArgoWorkflowsProperties(
    val url: String,
    val token: String,
)
