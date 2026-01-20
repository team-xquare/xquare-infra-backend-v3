package app.xquare.xquareinfra.adapters.inbound.web.environmentVariable

import app.xquare.xquareinfra.adapters.inbound.web.environmentVariable.dtos.request.SetEnvironmentVariableRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.environmentVariable.dtos.response.EnvironmentVariableResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.environmentVariable.dtos.response.ListEnvironmentVariablesResponseDto
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.*
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Environment variable")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/applications/{applicationId}/environment-variables")
class EnvironmentVariableController(
    private val setEnvironmentVariableUseCase: SetEnvironmentVariableUseCase,
    private val listEnvironmentVariablesUseCase: ListEnvironmentVariablesUseCase,
    private val deleteEnvironmentVariableUseCase: DeleteEnvironmentVariableUseCase,
) {
    @Operation(summary = "애플리케이션 환경변수 조회")
    @GetMapping
    fun listEnvironmentVariables(
        @PathVariable applicationId: Long,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<ListEnvironmentVariablesResponseDto> {
        val query =
            ListEnvironmentVariablesQuery(
                user = user,
                applicationId = applicationId,
            )

        val result = listEnvironmentVariablesUseCase.listEnvironmentVariables(query)

        return ListEnvironmentVariablesResponseDto(
            environmentVariables =
                result.environmentVariables.map {
                    EnvironmentVariableResponseDto(key = it.key, value = it.value)
                },
        ).toWrappedDto()
    }

    @Operation(summary = "애플리케이션 환경변수 추가 또는 수정")
    @PostMapping
    fun setEnvironmentVariable(
        @PathVariable applicationId: Long,
        @RequestBody @Valid request: SetEnvironmentVariableRequestDto,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<Unit> {
        val command =
            SetEnvironmentVariableCommand(
                user = user,
                applicationId = applicationId,
                key = request.name,
                value = request.value,
            )

        setEnvironmentVariableUseCase.setEnvironmentVariable(command)
        return APiWrappedResponseDto.success()
    }

    @Operation(summary = "애플리케이션 환경변수 삭제")
    @DeleteMapping("/{name}")
    fun deleteEnvironmentVariable(
        @PathVariable applicationId: Long,
        @PathVariable name: String,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<Unit> {
        val command =
            DeleteEnvironmentVariableCommand(
                user = user,
                applicationId = applicationId,
                key = name,
            )

        deleteEnvironmentVariableUseCase.deleteEnvironmentVariable(command)
        return APiWrappedResponseDto.success()
    }
}
