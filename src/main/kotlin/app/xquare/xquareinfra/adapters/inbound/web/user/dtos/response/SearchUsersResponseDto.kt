package app.xquare.xquareinfra.adapters.inbound.web.user.dtos.response

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class SearchUsersResponseDto(
    val user: List<UserResponseDto>,
) : SuccessResponseDto
