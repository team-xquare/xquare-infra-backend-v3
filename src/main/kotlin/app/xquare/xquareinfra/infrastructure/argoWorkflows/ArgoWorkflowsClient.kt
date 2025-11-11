package app.xquare.xquareinfra.infrastructure.argoWorkflows

import app.xquare.xquareinfra.infrastructure.argoWorkflows.dtos.GetWorkflowsResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "argoWorkflowsClient",
    url = "\${argo_workflows.url}",
)
interface ArgoWorkflowsClient {
    @GetMapping("/api/v1/workflows/{namespace}")
    fun getWorkflows(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable namespace: String,
        @RequestParam("listOptions.labelSelector") labelSelector: String? = null,
    ): ResponseEntity<GetWorkflowsResponse>
}
