package app.xquare.xquareinfra.infrastructure.gitops.manifest

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GitOpsGithubInfo(
    val owner: String,
    val repo: String,
    val branch: String,
    val installationId: String,
    val hash: String?,
    val triggerPaths: List<String>?,
)
