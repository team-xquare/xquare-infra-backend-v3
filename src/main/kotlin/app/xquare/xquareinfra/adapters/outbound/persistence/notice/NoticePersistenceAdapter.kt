package app.xquare.xquareinfra.adapters.outbound.persistence.notice

import app.xquare.xquareinfra.adapters.outbound.persistence.notice.mappers.toDomain
import app.xquare.xquareinfra.adapters.outbound.persistence.notice.mappers.toPersistence
import app.xquare.xquareinfra.application.notice.ports.outbound.NoticePersistenceForNoticePort
import app.xquare.xquareinfra.domain.notice.Notice
import app.xquare.xquareinfra.infrastructure.persistence.notice.repositories.NoticeRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class NoticePersistenceAdapter(
    private val noticeRepository: NoticeRepository,
) : NoticePersistenceForNoticePort {
    override fun save(notice: Notice): Notice {
        val entity = notice.toPersistence()
        val saved = noticeRepository.save(entity)
        return saved.toDomain()
    }

    override fun findById(noticeId: Long): Notice? = noticeRepository.findById(noticeId).getOrNull()?.toDomain()

    override fun findAll(
        page: Int,
        limit: Int,
    ): List<Notice> {
        val pageable = PageRequest.of(page, limit)
        return noticeRepository.findAll(pageable).content.map { it.toDomain() }
    }

    override fun delete(noticeId: Long) {
        noticeRepository.deleteById(noticeId)
    }
}
