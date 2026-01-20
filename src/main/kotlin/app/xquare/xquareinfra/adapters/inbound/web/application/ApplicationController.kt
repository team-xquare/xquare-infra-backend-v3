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
import app.xquare.xquareinfra.application.application.ports.inbound.*
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Application")
@RestController
@RequestMapping("/api/v1/applications")
class ApplicationController(
    private val createApplicationUseCase: CreateApplicationUseCase,
    private val getApplicationUseCase: GetApplicationUseCase,
    private val updateApplicationConfigurationUseCase: UpdateApplicationConfigurationUseCase,
    private val updateApplicationStatusUseCase: UpdateApplicationStatusUseCase,
    private val deleteApplicationUseCase: DeleteApplicationUseCase,
) {
    @Operation(summary = "애플리케이션 생성")
    @PostMapping
    fun createApplication(
        @RequestBody @Valid request: CreateApplicationRequestDto,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<CreateApplicationResponseDto> {
        val command =
            CreateApplicationCommand(
                userId = user.id!!,
                teamId = request.teamId,
                name = request.name,
                configuration = request.configuration.toDomain(),
            )
        val result = createApplicationUseCase.createApplication(command)
        return CreateApplicationResponseDto(result.applicationId).toWrappedDto()
    }

    @Operation(summary = "애플리케이션 조회")
    @GetMapping("/{applicationId}")
    fun getApplication(
        @PathVariable applicationId: Long,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<GetApplicationResponseDto> {
        val query =
            GetApplicationQuery(
                user = user,
                applicationId = applicationId,
            )
        val result = getApplicationUseCase.getApplication(query)
        return GetApplicationResponseDto(
            id = result.application.id!!,
            teamId = result.application.team.id!!,
            name = result.application.name,
            status = result.application.status.toDto(),
            configuration = result.application.configuration!!.toDto(),
        ).toWrappedDto()
    }

    @Operation(summary = "애플리케이션 설정 수정")
    @PutMapping("/{applicationId}/configuration")
    fun updateConfiguration(
        @PathVariable applicationId: Long,
        @RequestBody @Valid request: UpdateApplicationConfigurationRequestDto,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<Unit> {
        val command =
            UpdateApplicationConfigurationCommand(
                user = user,
                applicationId = applicationId,
                configuration = request.configuration.toDomain(),
            )
        updateApplicationConfigurationUseCase.updateApplicationConfiguration(command)
        return APiWrappedResponseDto.success()
    }

    @Operation(summary = "애플리케이션 배포 상태 수정 (관리자 전용)")
    @PutMapping("/{applicationId}/status")
    fun updateStatus(
        @PathVariable applicationId: Long,
        @RequestBody @Valid request: UpdateApplicationStatusRequestDto,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<Unit> {
        val command =
            UpdateApplicationStatusCommand(
                user = user,
                applicationId = applicationId,
                status = request.status.toDomain(),
            )
        updateApplicationStatusUseCase.updateApplicationStatus(command)
        return APiWrappedResponseDto.success()
    }

    @Operation(summary = "애플리케이션 삭제")
    @DeleteMapping("/{applicationId}")
    fun deleteApplication(
        @PathVariable applicationId: Long,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<Unit> {
        val command =
            DeleteApplicationCommand(
                applicationId = applicationId,
                user = user,
            )
        deleteApplicationUseCase.deleteApplication(command)
        return APiWrappedResponseDto.success()
    }
}
