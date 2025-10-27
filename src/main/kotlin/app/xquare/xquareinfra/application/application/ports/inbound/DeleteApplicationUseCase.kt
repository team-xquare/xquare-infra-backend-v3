package app.xquare.xquareinfra.application.application.ports.inbound

data class DeleteApplicationCommand(
    val userId: Long,
    val applicationId: Long,
)

data object DeleteApplicationResult

interface DeleteApplicationUseCase {
    fun deleteApplication(command: DeleteApplicationCommand): DeleteApplicationResult
}
