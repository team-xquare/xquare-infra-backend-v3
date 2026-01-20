package app.xquare.xquareinfra.adapters.inbound.web.deployment

import app.xquare.xquareinfra.adapters.inbound.web.deployment.dtos.common.toDto
import app.xquare.xquareinfra.adapters.inbound.web.deployment.dtos.response.DeploymentDto
import app.xquare.xquareinfra.adapters.inbound.web.deployment.dtos.response.GetDeploymentsResponseDto
import app.xquare.xquareinfra.application.deployment.ports.inbound.ListDeploymentsQuery
import app.xquare.xquareinfra.application.deployment.ports.inbound.ListDeploymentsUseCase
import app.xquare.xquareinfra.domain.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Deployment")
@RestController
@RequestMapping("/api/v1/applications/{applicationId}/deployments")
class DeploymentController(
    private val listDeploymentsUseCase: ListDeploymentsUseCase,
) {
    @Operation(summary = "애플리케이션 배포 조회")
    @GetMapping
    fun getDeployments(
        @PathVariable applicationId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") limit: Int,
        @AuthenticationPrincipal user: User,
    ): GetDeploymentsResponseDto {
        val query = ListDeploymentsQuery(user, applicationId, page, limit)

        val result = listDeploymentsUseCase.listDeployments(query)

        return GetDeploymentsResponseDto(
            result.deployments.map {
                DeploymentDto(
                    applicationId = applicationId,
                    commitHash = it.commitHash,
                    startedAt = it.startedAt,
                    finishedAt = it.finishedAt,
                    status = it.status.toDto(),
                )
            },
        )
    }
}
