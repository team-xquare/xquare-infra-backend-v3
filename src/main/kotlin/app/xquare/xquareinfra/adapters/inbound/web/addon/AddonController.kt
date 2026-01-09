package app.xquare.xquareinfra.adapters.inbound.web.addon

import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common.toDomain
import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common.toDto
import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.request.CreateAddonRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.request.UpdateAddonRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.response.CreateAddonResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.response.GetAddonResponseDto
import app.xquare.xquareinfra.application.addon.ports.inbound.CreateAddonCommand
import app.xquare.xquareinfra.application.addon.ports.inbound.CreateAddonUseCase
import app.xquare.xquareinfra.application.addon.ports.inbound.DeleteAddonCommand
import app.xquare.xquareinfra.application.addon.ports.inbound.DeleteAddonUseCase
import app.xquare.xquareinfra.application.addon.ports.inbound.GetAddonQuery
import app.xquare.xquareinfra.application.addon.ports.inbound.GetAddonUseCase
import app.xquare.xquareinfra.application.addon.ports.inbound.UpdateAddonCommand
import app.xquare.xquareinfra.application.addon.ports.inbound.UpdateAddonUseCase
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Addon")
@RestController
@RequestMapping("/api/v1/addons")
class AddonController(
    private val createAddonUseCase: CreateAddonUseCase,
    private val getAddonUseCase: GetAddonUseCase,
    private val updateAddonUseCase: UpdateAddonUseCase,
    private val deleteAddonUseCase: DeleteAddonUseCase,
) {
    @Operation(summary = "애드온 생성")
    @PostMapping
    fun createAddon(
        @RequestBody request: CreateAddonRequestDto,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<CreateAddonResponseDto> {
        val command =
            CreateAddonCommand(
                userId = user.id!!,
                teamId = request.teamId,
                name = request.name,
                type = request.type.toDomain(),
                storageGi = request.storageGi,
                configuration = request.configuration.toDomain(),
            )

        val result = createAddonUseCase.createAddon(command)
        return CreateAddonResponseDto(result.addonId).toWrappedDto()
    }

    @Operation(summary = "애드온 조회")
    @GetMapping("/{addonId}")
    fun getAddon(
        @PathVariable addonId: Long,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<GetAddonResponseDto> {
        val query =
            GetAddonQuery(
                userId = user.id!!,
                addonId = addonId,
            )

        val result = getAddonUseCase.getAddon(query)
        val addon = result.addon

        return GetAddonResponseDto(
            id = addon.id!!,
            name = addon.name,
            type = addon.type.toDto(),
            storageGi = addon.storageGi,
            configuration = addon.configuration.toDto(),
        ).toWrappedDto()
    }

    @Operation(summary = "애드온 수정")
    @PatchMapping("/{addonId}")
    fun updateAddon(
        @PathVariable addonId: Long,
        @RequestBody request: UpdateAddonRequestDto,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<Unit> {
        val command =
            UpdateAddonCommand(
                userId = user.id!!,
                addonId = addonId,
                storageGi = request.storageGi,
            )

        updateAddonUseCase.updateAddon(command)
        return APiWrappedResponseDto.success()
    }

    @Operation(summary = "애드온 삭제")
    @DeleteMapping("/{addonId}")
    fun deleteAddon(
        @PathVariable addonId: Long,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<Unit> {
        val command =
            DeleteAddonCommand(
                userId = user.id!!,
                addonId = addonId,
            )

        deleteAddonUseCase.deleteAddon(command)
        return APiWrappedResponseDto.success()
    }
}
