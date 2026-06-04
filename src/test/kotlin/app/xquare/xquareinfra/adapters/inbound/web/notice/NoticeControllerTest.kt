package app.xquare.xquareinfra.adapters.inbound.web.notice

import app.xquare.xquareinfra.adapters.inbound.web.notice.dtos.request.UpdateNoticeMultipartRequestDto
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
import app.xquare.xquareinfra.application.notice.ports.outbound.FileUploadPort
import app.xquare.xquareinfra.domain.notice.Notice
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.domain.user.UserRole
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile

class NoticeControllerTest {
    @Test
    fun `updateNoticeWithFile uploads file and forwards file update command`() {
        val fixture = createFixture()

        fixture.noticeController.updateNoticeWithFile(
            noticeId = 3L,
            user = adminUser(),
            request =
                UpdateNoticeMultipartRequestDto(
                    title = "수정 제목",
                    content = "수정 내용",
                    file = MockMultipartFile("file", "notice.pdf", "application/pdf", "file-body".toByteArray()),
                ),
        )

        assertEquals(listOf("notice.pdf"), fixture.fileUploadPort.uploadedFileNames)
        assertEquals(
            UpdateNoticeCommand(
                userId = 1L,
                noticeId = 3L,
                title = "수정 제목",
                content = "수정 내용",
                fileUrl = "https://files.test/notice.pdf",
                shouldUpdateFile = true,
            ),
            fixture.updateNoticeUseCase.receivedCommands.single(),
        )
    }

    @Test
    fun `updateNoticeWithFile can remove current file without uploading a new one`() {
        val fixture = createFixture()

        fixture.noticeController.updateNoticeWithFile(
            noticeId = 5L,
            user = adminUser(),
            request =
                UpdateNoticeMultipartRequestDto(
                    title = "수정 제목",
                    content = "수정 내용",
                    removeFile = true,
                ),
        )

        assertEquals(emptyList(), fixture.fileUploadPort.uploadedFileNames)
        assertEquals(
            UpdateNoticeCommand(
                userId = 1L,
                noticeId = 5L,
                title = "수정 제목",
                content = "수정 내용",
                fileUrl = null,
                shouldUpdateFile = true,
            ),
            fixture.updateNoticeUseCase.receivedCommands.single(),
        )
    }

    private fun createFixture(): Fixture {
        val updateNoticeUseCase = RecordingUpdateNoticeUseCase()
        val fileUploadPort = FakeFileUploadPort()

        return Fixture(
            noticeController =
                NoticeController(
                    createNoticeUseCase = NoOpCreateNoticeUseCase(),
                    updateNoticeUseCase = updateNoticeUseCase,
                    deleteNoticeUseCase = NoOpDeleteNoticeUseCase(),
                    listNoticesUseCase = NoOpListNoticesUseCase(),
                    getNoticeUseCase = NoOpGetNoticeUseCase(),
                    fileUploadPort = fileUploadPort,
                ),
            updateNoticeUseCase = updateNoticeUseCase,
            fileUploadPort = fileUploadPort,
        )
    }

    private fun adminUser(): User =
        User(
            id = 1L,
            username = "admin",
            password = "encoded-password",
            role = UserRole.ADMIN,
            studentNumber = 1101,
            name = "관리자",
            email = "admin@test.com",
        )

    private data class Fixture(
        val noticeController: NoticeController,
        val updateNoticeUseCase: RecordingUpdateNoticeUseCase,
        val fileUploadPort: FakeFileUploadPort,
    )

    private class RecordingUpdateNoticeUseCase : UpdateNoticeUseCase {
        val receivedCommands = mutableListOf<UpdateNoticeCommand>()

        override fun updateNotice(command: UpdateNoticeCommand): UpdateNoticeResult {
            receivedCommands += command
            return UpdateNoticeResult
        }
    }

    private class FakeFileUploadPort : FileUploadPort {
        val uploadedFileNames = mutableListOf<String>()

        override fun upload(file: MultipartFile): String {
            val originalFilename = file.originalFilename ?: "file"
            uploadedFileNames += originalFilename
            return "https://files.test/$originalFilename"
        }
    }

    private class NoOpCreateNoticeUseCase : CreateNoticeUseCase {
        override fun createNotice(command: CreateNoticeCommand): CreateNoticeResult = CreateNoticeResult(noticeId = 1L)
    }

    private class NoOpDeleteNoticeUseCase : DeleteNoticeUseCase {
        override fun deleteNotice(command: DeleteNoticeCommand): DeleteNoticeResult = DeleteNoticeResult
    }

    private class NoOpListNoticesUseCase : ListNoticesUseCase {
        override fun listNotices(query: ListNoticesQuery): ListNoticesResult = ListNoticesResult(emptyList())
    }

    private class NoOpGetNoticeUseCase : GetNoticeUseCase {
        override fun getNotice(query: GetNoticeQuery): GetNoticeResult =
            GetNoticeResult(
                Notice(
                    id = query.noticeId,
                    title = "공지",
                    content = "내용",
                    author = adminUser(),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
                ),
            )

        private fun adminUser(): User =
            User(
                id = 1L,
                username = "admin",
                password = "encoded-password",
                role = UserRole.ADMIN,
                studentNumber = 1101,
                name = "관리자",
                email = "admin@test.com",
            )
    }
}
