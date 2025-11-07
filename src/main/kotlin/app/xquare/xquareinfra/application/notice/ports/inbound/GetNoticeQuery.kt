package app.xquare.xquareinfra.application.notice.ports.inbound

import app.xquare.xquareinfra.domain.notice.Notice

data class GetNoticeQuery(
    val noticeId: Long,
)

data class GetNoticeResult(
    val notice: Notice,
)

interface GetNoticeUseCase {
    fun getNotice(query: GetNoticeQuery): GetNoticeResult
}
