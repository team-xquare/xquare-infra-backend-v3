package app.xquare.xquareinfra.application.notice.ports.inbound

data class CreateNoticeCommand(
    val userId: Long,
    val title: String,
    val content: String,
    val fileUrl: String? = null,
)

data class CreateNoticeResult(
    val noticeId: Long,
)

interface CreateNoticeUseCase {
    fun createNotice(command: CreateNoticeCommand): CreateNoticeResult
}
