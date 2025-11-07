package app.xquare.xquareinfra.adapters.inbound.web.user

import app.xquare.xquareinfra.adapters.inbound.web.user.dtos.CurrentUserResponseDto
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User")
@RestController
@RequestMapping("/api/v1/users")
class UserController {
    @Operation(summary = "현재 유저 조회")
    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<CurrentUserResponseDto> =
        CurrentUserResponseDto(
            id = user.id!!,
            username = user.username,
            role = user.role,
            studentNumber = user.studentNumber,
            name = user.name,
            email = user.email,
        ).toWrappedDto()
}
