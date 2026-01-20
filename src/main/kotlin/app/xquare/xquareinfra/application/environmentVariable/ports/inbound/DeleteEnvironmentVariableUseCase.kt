package app.xquare.xquareinfra.application.environmentVariable.ports.inbound

import app.xquare.xquareinfra.domain.user.User

data class DeleteEnvironmentVariableCommand(
    val user: User,
    val applicationId: Long,
    val key: String,
)

data object DeleteEnvironmentVariableResult

interface DeleteEnvironmentVariableUseCase {
    fun deleteEnvironmentVariable(command: DeleteEnvironmentVariableCommand): DeleteEnvironmentVariableResult
}
