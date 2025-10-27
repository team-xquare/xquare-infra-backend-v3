package app.xquare.xquareinfra.infrastructure.gitops.manifest

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class GitOpsEndpoint(
    val port: Int,
    val routes: List<String>?,
)
