package app.xquare.xquareinfra.application.notice

import app.xquare.xquareinfra.application.global.exception.CommonException
import app.xquare.xquareinfra.application.notice.ports.inbound.CreateNoticeCommand
import app.xquare.xquareinfra.application.notice.ports.inbound.CreateNoticeResult
import app.xquare.xquareinfra.application.notice.ports.inbound.CreateNoticeUseCase
import app.xquare.xquareinfra.application.notice.ports.inbound.DeleteNoticeCommand
import app.xquare.xquareinfra.application.notice.ports.inbound.DeleteNoticeResult
import app.xquare.xquareinfra.application.notice.ports.inbound.DeleteNoticeUseCase
import app.xquare.xquareinfra.application.notice.ports.inbound.GetNoticeQuery
import app.xquare.xquareinfra.application.notice.ports.inbound.GetNoticeResult
import app.xquare.xquareinfra.application.notice.ports.inbound.GetNoticeUseCase
import app.xquare.xquareinfra.application.notice.ports.inbound.ListNoticesQuery
import app.xquare.xquareinfra.application.notice.ports.inbound.ListNoticesResult
import app.xquare.xquareinfra.application.notice.ports.inbound.ListNoticesUseCase
import app.xquare.xquareinfra.application.notice.ports.inbound.UpdateNoticeCommand
import app.xquare.xquareinfra.application.notice.ports.inbound.UpdateNoticeResult
import app.xquare.xquareinfra.application.notice.ports.inbound.UpdateNoticeUseCase
import app.xquare.xquareinfra.application.notice.ports.outbound.NoticePersistenceForNoticePort
import app.xquare.xquareinfra.application.notice.ports.outbound.UserPersistenceForNoticePort
import app.xquare.xquareinfra.domain.notice.Notice
import app.xquare.xquareinfra.domain.user.UserRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class NoticeService(
    private val noticePersistencePort: NoticePersistenceForNoticePort,
    private val userPersistencePort: UserPersistenceForNoticePort,
) : CreateNoticeUseCase,
    UpdateNoticeUseCase,
    DeleteNoticeUseCase,
    GetNoticeUseCase,
    ListNoticesUseCase {
    override fun createNotice(command: CreateNoticeCommand): CreateNoticeResult {
        val user =
            userPersistencePort.findById(command.userId)
                ?: throw CommonException.UserNotFound

        if (user.role != UserRole.ADMIN) {
            throw CommonException.UnAuthorized
        }

        val notice =
            Notice(
                title = command.title,
                content = command.content,
                author = user,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
            )

        val savedNotice = noticePersistencePort.save(notice)
        return CreateNoticeResult(noticeId = savedNotice.id!!)
    }

    override fun updateNotice(command: UpdateNoticeCommand): UpdateNoticeResult {
        val user =
            userPersistencePort.findById(command.userId)
                ?: throw CommonException.UserNotFound

        if (user.role != UserRole.ADMIN) {
            throw CommonException.UnAuthorized
        }

        val notice =
            noticePersistencePort.findById(command.noticeId)
                ?: throw CommonException.NoticeNotFound

        val updatedNotice =
            notice.copy(
                title = command.title,
                content = command.content,
                updatedAt = LocalDateTime.now(),
            )

        noticePersistencePort.save(updatedNotice)
        return UpdateNoticeResult
    }

    override fun deleteNotice(command: DeleteNoticeCommand): DeleteNoticeResult {
        val user =
            userPersistencePort.findById(command.userId)
                ?: throw CommonException.UserNotFound

        if (user.role != UserRole.ADMIN) {
            throw CommonException.UnAuthorized
        }

        val notice =
            noticePersistencePort.findById(command.noticeId)
                ?: throw CommonException.NoticeNotFound

        noticePersistencePort.delete(notice.id!!)
        return DeleteNoticeResult
    }

    override fun getNotice(query: GetNoticeQuery): GetNoticeResult {
        val notice =
            noticePersistencePort.findById(query.noticeId)
                ?: throw CommonException.NoticeNotFound
        return GetNoticeResult(notice)
    }

    override fun listNotices(query: ListNoticesQuery): ListNoticesResult {
        val notices = noticePersistencePort.findAll(query.page, query.limit)
        return ListNoticesResult(notices)
    }
}
