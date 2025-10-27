package app.xquare.xquareinfra.infrastructure.gitops.manifest

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GitOpsProject(
    val applications: List<GitOpsApplication>?,
    val addons: List<GitOpsAddon>?,
)
