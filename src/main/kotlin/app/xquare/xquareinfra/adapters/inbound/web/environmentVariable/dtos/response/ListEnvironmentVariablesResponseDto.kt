package app.xquare.xquareinfra.adapters.inbound.web.environmentVariable.dtos.response

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class EnvironmentVariableResponseDto(
    val name: String,
)

data class ListEnvironmentVariablesResponseDto(
    val environmentVariables: List<EnvironmentVariableResponseDto>,
) : SuccessResponseDto
