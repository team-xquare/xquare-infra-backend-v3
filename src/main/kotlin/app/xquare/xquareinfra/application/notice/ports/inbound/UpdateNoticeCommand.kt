package app.xquare.xquareinfra.application.notice.ports.inbound

data class UpdateNoticeCommand(
    val userId: Long,
    val noticeId: Long,
    val title: String,
    val content: String,
    val fileUrl: String? = null,
    val shouldUpdateFile: Boolean = false,
)

data object UpdateNoticeResult

interface UpdateNoticeUseCase {
    fun updateNotice(command: UpdateNoticeCommand): UpdateNoticeResult
}
