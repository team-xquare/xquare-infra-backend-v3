package app.xquare.xquareinfra.application.notice

import app.xquare.xquareinfra.application.notice.ports.inbound.UpdateNoticeCommand
import app.xquare.xquareinfra.application.notice.ports.outbound.NoticePersistenceForNoticePort
import app.xquare.xquareinfra.application.notice.ports.outbound.UserPersistenceForNoticePort
import app.xquare.xquareinfra.domain.notice.Notice
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.domain.user.UserRole
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NoticeServiceTest {
    @Test
    fun `updateNotice keeps existing file when file update is not requested`() {
        val fixture = createFixture()
        val noticeId = fixture.noticePersistencePort.save(existingNotice(fileUrl = "https://files.test/original.pdf")).id!!

        fixture.noticeService.updateNotice(
            UpdateNoticeCommand(
                userId = ADMIN_ID,
                noticeId = noticeId,
                title = "수정된 제목",
                content = "수정된 내용",
            ),
        )

        val updatedNotice = fixture.noticePersistencePort.findById(noticeId)
        assertEquals("https://files.test/original.pdf", updatedNotice?.fileUrl)
    }

    @Test
    fun `updateNotice replaces existing file when new file url is provided`() {
        val fixture = createFixture()
        val noticeId = fixture.noticePersistencePort.save(existingNotice(fileUrl = "https://files.test/original.pdf")).id!!

        fixture.noticeService.updateNotice(
            UpdateNoticeCommand(
                userId = ADMIN_ID,
                noticeId = noticeId,
                title = "수정된 제목",
                content = "수정된 내용",
                fileUrl = "https://files.test/replaced.pdf",
                shouldUpdateFile = true,
            ),
        )

        val updatedNotice = fixture.noticePersistencePort.findById(noticeId)
        assertEquals("https://files.test/replaced.pdf", updatedNotice?.fileUrl)
    }

    @Test
    fun `updateNotice removes existing file when file removal is requested`() {
        val fixture = createFixture()
        val noticeId = fixture.noticePersistencePort.save(existingNotice(fileUrl = "https://files.test/original.pdf")).id!!

        fixture.noticeService.updateNotice(
            UpdateNoticeCommand(
                userId = ADMIN_ID,
                noticeId = noticeId,
                title = "수정된 제목",
                content = "수정된 내용",
                shouldUpdateFile = true,
            ),
        )

        val updatedNotice = fixture.noticePersistencePort.findById(noticeId)
        assertNull(updatedNotice?.fileUrl)
    }

    private fun createFixture(): Fixture {
        val noticePersistencePort = FakeNoticePersistenceForNoticePort()
        val userPersistencePort = FakeUserPersistenceForNoticePort().apply { save(adminUser()) }

        return Fixture(
            noticeService =
                NoticeService(
                    noticePersistencePort = noticePersistencePort,
                    userPersistencePort = userPersistencePort,
                ),
            noticePersistencePort = noticePersistencePort,
        )
    }

    private fun adminUser(): User =
        User(
            id = ADMIN_ID,
            username = "admin",
            password = "encoded-password",
            role = UserRole.ADMIN,
            studentNumber = 1101,
            name = "관리자",
            email = "admin@test.com",
        )

    private fun existingNotice(fileUrl: String?): Notice =
        Notice(
            title = "기존 제목",
            content = "기존 내용",
            author = adminUser(),
            fileUrl = fileUrl,
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = LocalDateTime.now().minusDays(1),
        )

    private data class Fixture(
        val noticeService: NoticeService,
        val noticePersistencePort: FakeNoticePersistenceForNoticePort,
    )

    private class FakeNoticePersistenceForNoticePort : NoticePersistenceForNoticePort {
        private val notices = linkedMapOf<Long, Notice>()
        private var nextId = 1L

        override fun save(notice: Notice): Notice {
            val savedNotice = notice.copy(id = notice.id ?: nextId++)
            notices[savedNotice.id!!] = savedNotice
            return savedNotice
        }

        override fun findById(noticeId: Long): Notice? = notices[noticeId]

        override fun findAll(
            page: Int,
            limit: Int,
        ): List<Notice> = notices.values.drop(page * limit).take(limit)

        override fun delete(noticeId: Long) {
            notices.remove(noticeId)
        }
    }

    private class FakeUserPersistenceForNoticePort : UserPersistenceForNoticePort {
        private val users = linkedMapOf<Long, User>()

        fun save(user: User) {
            users[user.id!!] = user
        }

        override fun findById(id: Long): User? = users[id]
    }

    private companion object {
        const val ADMIN_ID = 1L
    }
}
