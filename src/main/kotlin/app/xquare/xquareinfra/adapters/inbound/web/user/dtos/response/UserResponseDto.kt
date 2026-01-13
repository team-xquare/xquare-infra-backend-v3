package app.xquare.xquareinfra.adapters.inbound.web.user.dtos.response

import app.xquare.xquareinfra.domain.user.UserRole
import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class UserResponseDto(
    val id: Long,
    val username: String,
    val role: UserRole,
    val studentNumber: Int,
    val name: String,
    val email: String,
) : SuccessResponseDto
