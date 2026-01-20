package app.xquare.xquareinfra.application.application.ports.inbound

import app.xquare.xquareinfra.domain.user.User

data class DeleteApplicationCommand(
    val user: User,
    val applicationId: Long,
)

data object DeleteApplicationResult

interface DeleteApplicationUseCase {
    fun deleteApplication(command: DeleteApplicationCommand): DeleteApplicationResult
}
