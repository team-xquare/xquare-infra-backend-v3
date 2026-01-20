package app.xquare.xquareinfra.adapters.inbound.web.team

import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.common.toDto
import app.xquare.xquareinfra.adapters.inbound.web.addon.dtos.response.GetAddonResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration.toDto
import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.toDto
import app.xquare.xquareinfra.adapters.inbound.web.application.dtos.response.GetApplicationResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.toDomain
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.toDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.request.AddOrUpdateTeamMembersRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.request.CreateTeamRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.request.DeleteTeamMembersRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.request.UpdateTeamRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.response.*
import app.xquare.xquareinfra.application.team.ports.inbound.*
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@Tag(name = "Team")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/teams")
class TeamController(
    private val listTeamsUseCase: ListUserTeamsUseCase,
    private val getTeamUseCase: GetTeamUseCase,
    private val createTeamUseCase: CreateTeamUseCase,
    private val addOrUpdateMembersUseCase: AddOrUpdateMembersUseCase,
    private val deleteMembersUseCase: DeleteMembersUseCase,
    private val updateTeamUseCase: UpdateTeamUseCase,
    private val deleteTeamUseCase: DeleteTeamUseCase,
    private val listTeamApplicationsUseCase: ListTeamApplicationsUseCase,
    private val listTeamAddonsUseCase: ListTeamAddonsUseCase,
) {
    @Operation(summary = "현재 유저의 전체 팀 조회")
    @GetMapping
    fun listTeams(
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<GetTeamsResponseDto> {
        val query = ListUserTeamsQuery(user)
        val result = listTeamsUseCase.listUserTeams(query)

        return GetTeamsResponseDto(
            teams =
                result.teams.map {
                    GetTeamResponseDto(
                        id = it.id!!,
                        name = it.name,
                        type = it.type.toDto(),
                        members =
                            it.members.map { member ->
                                TeamMemberDto(userId = member.user.id!!, role = member.role.toDto())
                            },
                    )
                },
        ).toWrappedDto()
    }

    @Operation(summary = "팀 상세정보 조회")
    @ApiResponse(
        responseCode = "200",
        content = [Content(schema = Schema(implementation = CreateTeamResponseDto::class))],
    )
    @GetMapping("/{teamId}")
    fun getTeam(
        @PathVariable teamId: Long,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<GetTeamResponseDto> {
        val query = GetTeamQuery(user, teamId)
        val result = getTeamUseCase.getTeam(query)

        return GetTeamResponseDto(
            id = result.team.id!!,
            name = result.team.name,
            type = result.team.type.toDto(),
            members =
                result.team.members.map {
                    TeamMemberDto(userId = it.user.id!!, role = it.role.toDto())
                },
        ).toWrappedDto()
    }

    @Operation(summary = "팀의 모든 애플리케이션 조회")
    @GetMapping("/{teamId}/applications")
    fun getTeamApplications(
        @PathVariable teamId: Long,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<GetTeamApplicationsResponseDto> {
        val query = ListTeamApplicationsQuery(user, teamId)
        val result = listTeamApplicationsUseCase.listTeamApplications(query)

        return GetTeamApplicationsResponseDto(
            applications =
                result.applications.map {
                    GetApplicationResponseDto(
                        id = it.id!!,
                        teamId = it.team.id!!,
                        name = it.name,
                        status = it.status.toDto(),
                        configuration = it.configuration!!.toDto(),
                    )
                },
        ).toWrappedDto()
    }

    @Operation(summary = "팀의 모든 애드온 조회")
    @GetMapping("/{teamId}/addons")
    fun getTeamAddons(
        @PathVariable teamId: Long,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<GetTeamAddonsResponseDto> {
        val query = ListTeamAddonsQuery(user, teamId)
        val result = listTeamAddonsUseCase.listTeamAddons(query)

        return GetTeamAddonsResponseDto(
            addons =
                result.addons.map {
                    GetAddonResponseDto(
                        id = it.id!!,
                        name = it.name,
                        type = it.type.toDto(),
                        storageGi = it.storageGi,
                        configuration = it.configuration.toDto(),
                    )
                },
        ).toWrappedDto()
    }

    @Operation(summary = "팀 생성")
    @PostMapping
    fun createTeam(
        @RequestBody @Valid request: CreateTeamRequestDto,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<CreateTeamResponseDto> {
        val command =
            CreateTeamCommand(
                userId = user.id!!,
                name = request.name,
                type = request.type.toDomain(),
                initialMembers =
                    request.initialMembers.map {
                        CreateTeamCommand.InitialMember(it.id, it.role.toDomain())
                    },
            )

        val result = createTeamUseCase.createTeam(command)
        return CreateTeamResponseDto(result.teamId).toWrappedDto()
    }

    @Operation(summary = "팀 멤버 추가 또는 수정")
    @PatchMapping("/{teamId}/members")
    fun addOrUpdateMembers(
        @PathVariable teamId: Long,
        @RequestBody request: AddOrUpdateTeamMembersRequestDto,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<Unit> {
        val command =
            AddOrUpdateMembersCommand(
                user = user,
                teamId = teamId,
                members =
                    request.members.map {
                        AddOrUpdateMembersCommand.AddOrUpdateMember(it.id, it.role.toDomain())
                    },
            )

        addOrUpdateMembersUseCase.addOrUpdateMembers(command)
        return APiWrappedResponseDto.success()
    }

    @Operation(summary = "팀 멤버 삭제")
    @DeleteMapping("/{teamId}/members")
    fun deleteMembers(
        @PathVariable teamId: Long,
        @RequestBody request: DeleteTeamMembersRequestDto,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<Unit> {
        val command =
            DeleteMembersCommand(
                user = user,
                teamId = teamId,
                memberIds = request.ids,
            )

        deleteMembersUseCase.deleteMembers(command)
        return APiWrappedResponseDto.success()
    }

    @Operation(summary = "팀 정보 수정")
    @PatchMapping("/{teamId}")
    fun updateTeam(
        @PathVariable teamId: Long,
        @RequestBody request: UpdateTeamRequestDto,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<Unit> {
        val command =
            UpdateTeamCommand(
                user = user,
                teamId = teamId,
                type = request.type?.toDomain(),
            )

        updateTeamUseCase.updateTeam(command)
        return APiWrappedResponseDto.success()
    }

    @Operation(summary = "팀 삭제")
    @DeleteMapping("/{teamId}")
    fun deleteTeam(
        @PathVariable teamId: Long,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<Unit> {
        val command = DeleteTeamCommand(user, teamId)
        deleteTeamUseCase.deleteTeam(command)
        return APiWrappedResponseDto.success()
    }
}
