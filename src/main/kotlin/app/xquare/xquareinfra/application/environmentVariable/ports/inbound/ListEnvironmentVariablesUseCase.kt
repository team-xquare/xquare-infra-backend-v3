package app.xquare.xquareinfra.application.environmentVariable.ports.inbound

import app.xquare.xquareinfra.domain.user.User

data class ListEnvironmentVariablesQuery(
    val user: User,
    val applicationId: Long,
)

data class EnvironmentVariableSummary(
    val key: String,
    val value: String,
)

data class ListEnvironmentVariablesResult(
    val environmentVariables: List<EnvironmentVariableSummary>,
)

interface ListEnvironmentVariablesUseCase {
    fun listEnvironmentVariables(query: ListEnvironmentVariablesQuery): ListEnvironmentVariablesResult
}
