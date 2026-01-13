package app.xquare.xquareinfra.application.environmentVariable.ports.inbound

data class ListEnvironmentVariablesQuery(
    val userId: Long,
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
