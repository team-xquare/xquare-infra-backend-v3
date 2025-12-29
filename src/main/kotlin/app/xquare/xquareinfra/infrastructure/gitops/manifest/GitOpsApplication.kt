package app.xquare.xquareinfra.infrastructure.gitops.manifest

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GitOpsApplication(
    val name: String,
    val github: GitOpsGithubInfo,
    val build: GitOpsBuild,
    val endpoints: List<GitOpsEndpoint>?,
)
