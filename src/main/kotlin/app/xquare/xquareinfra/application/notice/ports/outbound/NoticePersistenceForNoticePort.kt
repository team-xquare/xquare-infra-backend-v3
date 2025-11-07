package app.xquare.xquareinfra.application.notice.ports.outbound

import app.xquare.xquareinfra.domain.notice.Notice

interface NoticePersistenceForNoticePort {
    fun save(notice: Notice): Notice

    fun findById(noticeId: Long): Notice?

    fun findAll(
        page: Int,
        limit: Int,
    ): List<Notice>

    fun delete(noticeId: Long)
}
