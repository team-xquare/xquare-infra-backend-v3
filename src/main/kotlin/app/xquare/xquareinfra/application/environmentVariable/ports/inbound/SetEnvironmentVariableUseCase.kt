package app.xquare.xquareinfra.application.environmentVariable.ports.inbound

import app.xquare.xquareinfra.domain.user.User

data class SetEnvironmentVariableCommand(
    val user: User,
    val applicationId: Long,
    val key: String,
    val value: String,
)

data object SetEnvironmentVariableResult

interface SetEnvironmentVariableUseCase {
    fun setEnvironmentVariable(command: SetEnvironmentVariableCommand): SetEnvironmentVariableResult
}
