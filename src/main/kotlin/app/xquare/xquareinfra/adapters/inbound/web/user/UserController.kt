package app.xquare.xquareinfra.adapters.inbound.web.user

import app.xquare.xquareinfra.adapters.inbound.web.user.dtos.CurrentUserResponseDto
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController {
    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            CurrentUserResponseDto(
                id = user.id!!,
                username = user.username,
                role = user.role,
                studentNumber = user.studentNumber,
                name = user.name,
                email = user.email,
            ).toWrappedDto(),
        )
}
