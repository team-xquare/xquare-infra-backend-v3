package app.xquare.xquareinfra.adapters.inbound.web.team

import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.toDomain
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.common.toDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.request.AddOrUpdateTeamMembersRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.request.CreateTeamRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.request.DeleteTeamMembersRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.request.UpdateTeamRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.response.CreateTeamResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.response.GetTeamResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.response.GetTeamsResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.response.TeamMemberDto
import app.xquare.xquareinfra.adapters.inbound.web.team.dtos.response.TeamSummaryResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.team.errorCodes.TeamErrorCode
import app.xquare.xquareinfra.application.team.ports.inbound.AddOrUpdateMembersCommand
import app.xquare.xquareinfra.application.team.ports.inbound.AddOrUpdateMembersResult
import app.xquare.xquareinfra.application.team.ports.inbound.AddOrUpdateMembersUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.CreateTeamCommand
import app.xquare.xquareinfra.application.team.ports.inbound.CreateTeamResult
import app.xquare.xquareinfra.application.team.ports.inbound.CreateTeamUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.DeleteMembersCommand
import app.xquare.xquareinfra.application.team.ports.inbound.DeleteMembersResult
import app.xquare.xquareinfra.application.team.ports.inbound.DeleteMembersUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.DeleteTeamCommand
import app.xquare.xquareinfra.application.team.ports.inbound.DeleteTeamResult
import app.xquare.xquareinfra.application.team.ports.inbound.DeleteTeamUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.GetTeamQuery
import app.xquare.xquareinfra.application.team.ports.inbound.GetTeamResult
import app.xquare.xquareinfra.application.team.ports.inbound.GetTeamUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamsQuery
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamsResult
import app.xquare.xquareinfra.application.team.ports.inbound.ListTeamsUseCase
import app.xquare.xquareinfra.application.team.ports.inbound.UpdateTeamCommand
import app.xquare.xquareinfra.application.team.ports.inbound.UpdateTeamResult
import app.xquare.xquareinfra.application.team.ports.inbound.UpdateTeamUseCase
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import app.xquare.xquareinfra.infrastructure.web.toWrappedDto
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/api/teams")
class TeamController(
    private val listTeamsUseCase: ListTeamsUseCase,
    private val getTeamUseCase: GetTeamUseCase,
    private val createTeamUseCase: CreateTeamUseCase,
    private val addOrUpdateMembersUseCase: AddOrUpdateMembersUseCase,
    private val deleteMembersUseCase: DeleteMembersUseCase,
    private val updateTeamUseCase: UpdateTeamUseCase,
    private val deleteTeamUseCase: DeleteTeamUseCase,
) {
    @GetMapping
    fun listTeams(
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val query = ListTeamsQuery(user.id!!)

        return when (val result = listTeamsUseCase.listTeams(query)) {
            is ListTeamsResult.Success ->
                ResponseEntity.ok(
                    GetTeamsResponseDto(
                        teams =
                            result.teams.map
                                { TeamSummaryResponseDto(id = it.id!!, name = it.name, type = it.type.toDto()) },
                    ).toWrappedDto(),
                )
        }
    }

    @GetMapping("/{teamId}")
    fun getTeam(
        @PathVariable teamId: Long,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val query = GetTeamQuery(user.id!!, teamId)

        return when (val result = getTeamUseCase.getTeam(query)) {
            is GetTeamResult.Success ->
                ResponseEntity.ok(
                    GetTeamResponseDto(
                        id = result.team.id!!,
                        name = result.team.name,
                        type = result.team.type.toDto(),
                        members =
                            result.team.members.map { member ->
                                TeamMemberDto(
                                    userId = member.user.id!!,
                                    role = member.role.toDto(),
                                )
                            },
                    ).toWrappedDto(),
                )
            is GetTeamResult.TeamNotFound ->
                ResponseEntity.status(404).body(
                    TeamErrorCode.TEAM_NOT_FOUND.toWrappedDto(),
                )
            is GetTeamResult.NotTeamMember ->
                ResponseEntity.status(403).body(
                    TeamErrorCode.INVALID_ACCESS.toWrappedDto(),
                )
        }
    }

    @PostMapping
    fun createTeam(
        @RequestBody request: CreateTeamRequestDto,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
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

        return when (val result = createTeamUseCase.createTeam(command)) {
            is CreateTeamResult.Success ->
                ResponseEntity.ok(
                    CreateTeamResponseDto(result.teamId).toWrappedDto(),
                )
            is CreateTeamResult.TeamNameAlreadyExists ->
                ResponseEntity.badRequest().body(
                    TeamErrorCode.TEAM_ALREADY_EXISTS.toWrappedDto(),
                )
            is CreateTeamResult.UserNotFound ->
                ResponseEntity.badRequest().body(
                    TeamErrorCode.INVALID_MEMBER.toWrappedDto(),
                )
        }
    }

    @PatchMapping("/{teamId}/members")
    fun addOrUpdateMembers(
        @PathVariable teamId: Long,
        @RequestBody request: AddOrUpdateTeamMembersRequestDto,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val command =
            AddOrUpdateMembersCommand(
                userId = user.id!!,
                teamId = teamId,
                members =
                    request.members.map {
                        AddOrUpdateMembersCommand.AddOrUpdateMember(it.id, it.role.toDomain())
                    },
            )

        return when (addOrUpdateMembersUseCase.addOrUpdateMembers(command)) {
            is AddOrUpdateMembersResult.Success ->
                ResponseEntity.ok().body(
                    APiWrappedResponseDto.success(),
                )
            is AddOrUpdateMembersResult.TeamNotFound ->
                ResponseEntity.status(404).body(
                    TeamErrorCode.TEAM_NOT_FOUND.toWrappedDto(),
                )
            is AddOrUpdateMembersResult.NotAdmin ->
                ResponseEntity.status(403).body(
                    TeamErrorCode.INVALID_ACCESS.toWrappedDto(),
                )
            is AddOrUpdateMembersResult.UserNotFound ->
                ResponseEntity.badRequest().body(
                    TeamErrorCode.INVALID_MEMBER.toWrappedDto(),
                )
        }
    }

    @DeleteMapping("/{teamId}/members")
    fun deleteMembers(
        @PathVariable teamId: Long,
        @RequestBody request: DeleteTeamMembersRequestDto,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val command =
            DeleteMembersCommand(
                userId = user.id!!,
                teamId = teamId,
                memberIds = request.ids,
            )

        return when (deleteMembersUseCase.deleteMembers(command)) {
            is DeleteMembersResult.Success ->
                ResponseEntity.ok().body(
                    APiWrappedResponseDto.success(),
                )
            is DeleteMembersResult.TeamNotFound ->
                ResponseEntity.status(404).body(
                    TeamErrorCode.TEAM_NOT_FOUND.toWrappedDto(),
                )
            is DeleteMembersResult.NotAdmin ->
                ResponseEntity.status(403).body(
                    TeamErrorCode.INVALID_ACCESS.toWrappedDto(),
                )
        }
    }

    @PatchMapping("/{teamId}")
    fun updateTeam(
        @PathVariable teamId: Long,
        @RequestBody request: UpdateTeamRequestDto,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val command =
            UpdateTeamCommand(
                userId = user.id!!,
                teamId = teamId,
                name = request.name,
                type = request.type?.toDomain(),
            )

        return when (updateTeamUseCase.updateTeam(command)) {
            is UpdateTeamResult.Success ->
                ResponseEntity.ok().body(
                    APiWrappedResponseDto.success(),
                )
            is UpdateTeamResult.TeamNotFound ->
                ResponseEntity.status(404).body(
                    TeamErrorCode.TEAM_NOT_FOUND.toWrappedDto(),
                )
            is UpdateTeamResult.NotAdmin ->
                ResponseEntity.status(403).body(
                    TeamErrorCode.INVALID_ACCESS.toWrappedDto(),
                )
        }
    }

    @DeleteMapping("/{teamId}")
    fun deleteTeam(
        @PathVariable teamId: Long,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> {
        val command =
            DeleteTeamCommand(
                userId = user.id!!,
                teamId = teamId,
            )

        return when (deleteTeamUseCase.deleteTeam(command)) {
            is DeleteTeamResult.Success ->
                ResponseEntity.ok().body(
                    APiWrappedResponseDto.success(),
                )
            is DeleteTeamResult.TeamNotFound ->
                ResponseEntity.status(404).body(
                    TeamErrorCode.TEAM_NOT_FOUND.toWrappedDto(),
                )
            is DeleteTeamResult.NotAdmin ->
                ResponseEntity.status(403).body(
                    TeamErrorCode.INVALID_ACCESS.toWrappedDto(),
                )
        }
    }
}
