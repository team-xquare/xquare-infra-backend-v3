package app.xquare.xquareinfra.application.notice.ports.inbound

data class UpdateNoticeCommand(
    val userId: Long,
    val noticeId: Long,
    val title: String,
    val content: String,
)

data object UpdateNoticeResult

interface UpdateNoticeUseCase {
    fun updateNotice(command: UpdateNoticeCommand): UpdateNoticeResult
}
