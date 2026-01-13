package app.xquare.xquareinfra.adapters.inbound.web.user

import app.xquare.xquareinfra.adapters.inbound.web.user.dtos.response.SearchUsersResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.user.dtos.response.UserResponseDto
import app.xquare.xquareinfra.application.user.ports.inbound.SearchUsersQuery
import app.xquare.xquareinfra.application.user.ports.inbound.SearchUsersUseCase
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@Tag(name = "User")
@RestController
@RequestMapping("/api/v1/users")
class UserController(
    val searchUsersUseCase: SearchUsersUseCase,
) {
    @Operation(summary = "현재 유저 조회")
    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<UserResponseDto> =
        UserResponseDto(
            id = user.id!!,
            username = user.username,
            role = user.role,
            studentNumber = user.studentNumber,
            name = user.name,
            email = user.email,
        ).toWrappedDto()

    @Operation(summary = "유저 검색")
    @GetMapping("/search")
    fun searchUsers(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) email: String?,
    ): APiWrappedResponseDto<SearchUsersResponseDto> {
        if (name.isNullOrBlank() && email.isNullOrBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email or name must be provided")
        }

        val query =
            SearchUsersQuery(
                name = name,
                email = email,
            )

        val result = searchUsersUseCase.searchUsers(query)
        return SearchUsersResponseDto(
            result.users.map {
                UserResponseDto(
                    id = it.id!!,
                    username = it.username,
                    role = it.role,
                    studentNumber = it.studentNumber,
                    name = it.name,
                    email = it.email,
                )
            },
        ).toWrappedDto()
    }
}
