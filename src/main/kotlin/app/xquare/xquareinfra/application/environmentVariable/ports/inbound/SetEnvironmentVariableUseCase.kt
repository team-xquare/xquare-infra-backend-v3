package app.xquare.xquareinfra.application.environmentVariable.ports.inbound

data class SetEnvironmentVariableCommand(
    val userId: Long,
    val applicationId: Long,
    val key: String,
    val value: String,
)

data object SetEnvironmentVariableResult

interface SetEnvironmentVariableUseCase {
    fun setEnvironmentVariable(command: SetEnvironmentVariableCommand): SetEnvironmentVariableResult
}
