package app.xquare.xquareinfra.adapters.inbound.web.application

import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.UpdateApplicationConfigurationRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.UpdateApplicationStatusRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration.toDomain
import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration.toDto
import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.toDomain
import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.toDto
import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.request.CreateApplicationRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.response.CreateApplicationResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.response.GetApplicationResponseDto
import app.xquare.xquareinfra.application.application.ports.inbound.CreateApplicationCommand
import app.xquare.xquareinfra.application.application.ports.inbound.CreateApplicationUseCase
import app.xquare.xquareinfra.application.application.ports.inbound.DeleteApplicationCommand
import app.xquare.xquareinfra.application.application.ports.inbound.DeleteApplicationUseCase
import app.xquare.xquareinfra.application.application.ports.inbound.GetApplicationQuery
import app.xquare.xquareinfra.application.application.ports.inbound.GetApplicationUseCase
import app.xquare.xquareinfra.application.application.ports.inbound.UpdateApplicationConfigurationCommand
import app.xquare.xquareinfra.application.application.ports.inbound.UpdateApplicationConfigurationUseCase
import app.xquare.xquareinfra.application.application.ports.inbound.UpdateApplicationStatusCommand
import app.xquare.xquareinfra.application.application.ports.inbound.UpdateApplicationStatusUseCase
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/applications")
class ApplicationController(
    private val createApplicationUseCase: CreateApplicationUseCase,
    private val getApplicationUseCase: GetApplicationUseCase,
    private val updateApplicationConfigurationUseCase: UpdateApplicationConfigurationUseCase,
    private val updateApplicationStatusUseCase: UpdateApplicationStatusUseCase,
    private val deleteApplicationUseCase: DeleteApplicationUseCase,
) {
    @PostMapping
    fun createApplication(
        @RequestBody @Valid request: CreateApplicationRequestDto,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val command =
            CreateApplicationCommand(
                userId = user.id!!,
                teamId = request.teamId,
                name = request.name,
                configuration = request.configuration.toDomain(),
            )

        val result = createApplicationUseCase.createApplication(command)
        return ResponseEntity.ok(CreateApplicationResponseDto(result.applicationId).toWrappedDto())
    }

    @GetMapping("/{applicationId}")
    fun getApplication(
        @PathVariable applicationId: Long,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val query =
            GetApplicationQuery(
                userId = user.id!!,
                applicationId = applicationId,
            )

        val result = getApplicationUseCase.getApplication(query)
        return ResponseEntity.ok(
            GetApplicationResponseDto(
                id = result.application.id!!,
                teamId = result.application.team.id!!,
                name = result.application.name,
                status = result.application.status.toDto(),
                configuration = result.application.configuration.toDto(),
            ).toWrappedDto(),
        )
    }

    @PutMapping("/{applicationId}/configuration")
    fun updateConfiguration(
        @PathVariable applicationId: Long,
        @RequestBody @Valid request: UpdateApplicationConfigurationRequestDto,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val command =
            UpdateApplicationConfigurationCommand(
                userId = user.id!!,
                applicationId = applicationId,
                configuration = request.configuration.toDomain(),
            )

        updateApplicationConfigurationUseCase.updateApplicationConfiguration(command)
        return ResponseEntity.ok(APiWrappedResponseDto.success())
    }

    @PutMapping("/{applicationId}/status")
    fun updateStatus(
        @PathVariable applicationId: Long,
        @RequestBody @Valid request: UpdateApplicationStatusRequestDto,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val command =
            UpdateApplicationStatusCommand(
                userId = user.id!!,
                applicationId = applicationId,
                status = request.status.toDomain(),
            )

        updateApplicationStatusUseCase.updateApplicationStatus(command)
        return ResponseEntity.ok(APiWrappedResponseDto.success())
    }

    @DeleteMapping("/{applicationId}")
    fun deleteApplication(
        @PathVariable applicationId: Long,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val command =
            DeleteApplicationCommand(
                applicationId = applicationId,
                userId = user.id!!,
            )

        deleteApplicationUseCase.deleteApplication(command)
        return ResponseEntity.ok(APiWrappedResponseDto.success())
    }
}
