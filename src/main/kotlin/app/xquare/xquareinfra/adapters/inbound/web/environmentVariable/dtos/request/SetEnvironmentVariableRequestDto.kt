package app.xquare.xquareinfra.adapters.inbound.web.environmentVariable.dtos.request

data class SetEnvironmentVariableRequestDto(
    val name: String,
    val value: String,
)
