package app.xquare.xquareinfra.adapters.inbound.web.environmentVariable

import app.xquare.xquareinfra.adapters.inbound.web.environmentVariable.dtos.request.SetEnvironmentVariableRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.environmentVariable.dtos.response.EnvironmentVariableResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.environmentVariable.dtos.response.ListEnvironmentVariablesResponseDto
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.DeleteEnvironmentVariableCommand
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.DeleteEnvironmentVariableUseCase
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.ListEnvironmentVariablesQuery
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.ListEnvironmentVariablesUseCase
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.SetEnvironmentVariableCommand
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.SetEnvironmentVariableUseCase
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/applications/{applicationId}/environment-variables")
class EnvironmentVariableController(
    private val setEnvironmentVariableUseCase: SetEnvironmentVariableUseCase,
    private val listEnvironmentVariablesUseCase: ListEnvironmentVariablesUseCase,
    private val deleteEnvironmentVariableUseCase: DeleteEnvironmentVariableUseCase,
) {
    @GetMapping
    fun listEnvironmentVariables(
        @PathVariable applicationId: Long,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val query =
            ListEnvironmentVariablesQuery(
                userId = user.id!!,
                applicationId = applicationId,
            )

        val result = listEnvironmentVariablesUseCase.listEnvironmentVariables(query)
        return ResponseEntity.ok(
            ListEnvironmentVariablesResponseDto(
                environmentVariables =
                    result.environmentVariables.map {
                        EnvironmentVariableResponseDto(name = it.key)
                    },
            ).toWrappedDto(),
        )
    }

    @PostMapping
    fun setEnvironmentVariable(
        @PathVariable applicationId: Long,
        @RequestBody @Valid request: SetEnvironmentVariableRequestDto,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val command =
            SetEnvironmentVariableCommand(
                userId = user.id!!,
                applicationId = applicationId,
                key = request.name,
                value = request.value,
            )

        setEnvironmentVariableUseCase.setEnvironmentVariable(command)
        return ResponseEntity.ok(APiWrappedResponseDto.success())
    }

    @DeleteMapping("/{name}")
    fun deleteEnvironmentVariable(
        @PathVariable applicationId: Long,
        @PathVariable name: String,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val command =
            DeleteEnvironmentVariableCommand(
                userId = user.id!!,
                applicationId = applicationId,
                key = name,
            )

        deleteEnvironmentVariableUseCase.deleteEnvironmentVariable(command)
        return ResponseEntity.ok(APiWrappedResponseDto.success())
    }
}
