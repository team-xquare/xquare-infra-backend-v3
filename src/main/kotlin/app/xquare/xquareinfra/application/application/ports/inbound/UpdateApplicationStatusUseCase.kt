package app.xquare.xquareinfra.application.application.ports.inbound

import app.xquare.xquareinfra.domain.application.ApplicationStatus
import app.xquare.xquareinfra.domain.user.User

data class UpdateApplicationStatusCommand(
    val user: User,
    val applicationId: Long,
    val status: ApplicationStatus,
)

data object UpdateApplicationStatusResult

interface UpdateApplicationStatusUseCase {
    fun updateApplicationStatus(command: UpdateApplicationStatusCommand): UpdateApplicationStatusResult
}
