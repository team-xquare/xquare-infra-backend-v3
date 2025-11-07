package app.xquare.xquareinfra.infrastructure.persistence.notice.repositories

import app.xquare.xquareinfra.infrastructure.persistence.notice.schema.NoticePersistenceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NoticeRepository : JpaRepository<NoticePersistenceEntity, Long>
