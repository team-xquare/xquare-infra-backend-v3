package app.xquare.xquareinfra.application.application.ports.inbound

import app.xquare.xquareinfra.domain.application.ApplicationStatus

data class UpdateApplicationStatusCommand(
    val userId: Long,
    val applicationId: Long,
    val status: ApplicationStatus,
)

data object UpdateApplicationStatusResult

interface UpdateApplicationStatusUseCase {
    fun updateApplicationStatus(command: UpdateApplicationStatusCommand): UpdateApplicationStatusResult
}
