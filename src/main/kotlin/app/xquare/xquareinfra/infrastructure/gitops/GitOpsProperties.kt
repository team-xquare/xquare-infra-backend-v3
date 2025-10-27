package app.xquare.xquareinfra.infrastructure.gitops

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "gitops")
data class GitOpsProperties(
    val token: String,
    val owner: String,
    val repo: String,
    val branch: String,
)
