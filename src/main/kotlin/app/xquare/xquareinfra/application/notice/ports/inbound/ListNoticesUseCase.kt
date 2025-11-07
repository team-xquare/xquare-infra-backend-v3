package app.xquare.xquareinfra.application.notice.ports.inbound

import app.xquare.xquareinfra.domain.notice.Notice

data class ListNoticesQuery(
    val page: Int,
    val limit: Int,
)

data class ListNoticesResult(
    val notices: List<Notice>,
)

interface ListNoticesUseCase {
    fun listNotices(query: ListNoticesQuery): ListNoticesResult
}
