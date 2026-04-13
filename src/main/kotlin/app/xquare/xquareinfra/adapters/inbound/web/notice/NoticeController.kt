package app.xquare.xquareinfra.adapters.inbound.web.notice

import app.xquare.xquareinfra.adapters.inbound.web.notice.dtos.request.CreateNoticeRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.notice.dtos.request.UpdateNoticeRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.notice.dtos.response.CreateNoticeResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.notice.dtos.response.GetNoticeResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.notice.dtos.response.ListNoticesResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.notice.dtos.response.NoticeSummaryResponseDto
import app.xquare.xquareinfra.application.notice.ports.inbound.CreateNoticeCommand
import app.xquare.xquareinfra.application.notice.ports.inbound.CreateNoticeUseCase
import app.xquare.xquareinfra.application.notice.ports.inbound.DeleteNoticeCommand
import app.xquare.xquareinfra.application.notice.ports.inbound.DeleteNoticeUseCase
import app.xquare.xquareinfra.application.notice.ports.inbound.GetNoticeQuery
import app.xquare.xquareinfra.application.notice.ports.inbound.GetNoticeUseCase
import app.xquare.xquareinfra.application.notice.ports.inbound.ListNoticesQuery
import app.xquare.xquareinfra.application.notice.ports.inbound.ListNoticesUseCase
import app.xquare.xquareinfra.application.notice.ports.inbound.UpdateNoticeCommand
import app.xquare.xquareinfra.application.notice.ports.inbound.UpdateNoticeUseCase
import app.xquare.xquareinfra.application.notice.ports.outbound.FileUploadPort
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Notice")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/notices")
class NoticeController(
    private val createNoticeUseCase: CreateNoticeUseCase,
    private val updateNoticeUseCase: UpdateNoticeUseCase,
    private val deleteNoticeUseCase: DeleteNoticeUseCase,
    private val listNoticesUseCase: ListNoticesUseCase,
    private val getNoticeUseCase: GetNoticeUseCase,
    private val fileUploadPort: FileUploadPort,
) {
    @Operation(summary = "공지 생성")
    @PostMapping
    fun createNotice(
        @AuthenticationPrincipal user: User,
        @RequestBody request: CreateNoticeRequestDto,
    ): APiWrappedResponseDto<CreateNoticeResponseDto> {
        val command =
            CreateNoticeCommand(
                userId = user.id!!,
                title = request.title,
                content = request.content,
            )

        val result = createNoticeUseCase.createNotice(command)
        return CreateNoticeResponseDto(result.noticeId).toWrappedDto()
    }

    @Operation(summary = "공지 수정")
    @PutMapping("/{noticeId}")
    fun updateNotice(
        @PathVariable noticeId: Long,
        @AuthenticationPrincipal user: User,
        @RequestBody request: UpdateNoticeRequestDto,
    ): APiWrappedResponseDto<Unit> {
        val command =
            UpdateNoticeCommand(
                userId = user.id!!,
                noticeId = noticeId,
                title = request.title,
                content = request.content,
            )

        updateNoticeUseCase.updateNotice(command)
        return APiWrappedResponseDto.success()
    }

    @Operation(summary = "공지 삭제")
    @DeleteMapping("/{noticeId}")
    fun deleteNotice(
        @PathVariable noticeId: Long,
        @AuthenticationPrincipal user: User,
    ): APiWrappedResponseDto<Unit> {
        val command =
            DeleteNoticeCommand(
                userId = user.id!!,
                noticeId = noticeId,
            )

        deleteNoticeUseCase.deleteNotice(command)
        return APiWrappedResponseDto.success()
    }

    @Operation(summary = "공지 목록 조회")
    @GetMapping
    fun listNotices(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") limit: Int,
    ): APiWrappedResponseDto<ListNoticesResponseDto> {
        val query = ListNoticesQuery(page, limit)
        val result = listNoticesUseCase.listNotices(query)

        return ListNoticesResponseDto(
            notices =
                result.notices.map {
                    NoticeSummaryResponseDto(
                        id = it.id!!,
                        title = it.title,
                        author = it.author.name,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt,
                    )
                },
        ).toWrappedDto()
    }

    @Operation(summary = "공지 조회")
    @GetMapping("/{noticeId}")
    fun getNotice(
        @PathVariable noticeId: Long,
    ): APiWrappedResponseDto<GetNoticeResponseDto> {
        val query = GetNoticeQuery(noticeId)
        val result = getNoticeUseCase.getNotice(query)

        return GetNoticeResponseDto(
            id = result.notice.id!!,
            title = result.notice.title,
            content = result.notice.content,
            author = result.notice.author.name,
            fileUrl = result.notice.fileUrl,
            createdAt = result.notice.createdAt,
            updatedAt = result.notice.updatedAt,
        ).toWrappedDto()
    }
}
