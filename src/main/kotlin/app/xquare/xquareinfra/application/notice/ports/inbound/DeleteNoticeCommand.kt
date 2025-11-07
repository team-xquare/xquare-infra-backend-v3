package app.xquare.xquareinfra.application.notice.ports.inbound

data class DeleteNoticeCommand(
    val userId: Long,
    val noticeId: Long,
)

data object DeleteNoticeResult

interface DeleteNoticeUseCase {
    fun deleteNotice(command: DeleteNoticeCommand): DeleteNoticeResult
}
