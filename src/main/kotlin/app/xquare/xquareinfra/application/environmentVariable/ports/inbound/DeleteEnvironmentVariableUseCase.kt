package app.xquare.xquareinfra.application.environmentVariable.ports.inbound

data class DeleteEnvironmentVariableCommand(
    val userId: Long,
    val applicationId: Long,
    val key: String,
)

data object DeleteEnvironmentVariableResult

interface DeleteEnvironmentVariableUseCase {
    fun deleteEnvironmentVariable(command: DeleteEnvironmentVariableCommand): DeleteEnvironmentVariableResult
}
