package app.xquare.xquareinfra.adapters.inbound.web.notice.dtos.response

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto
import java.time.LocalDateTime

data class GetNoticeResponseDto(
    val id: Long,
    val title: String,
    val content: String,
    val author: String,
    val fileUrl: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) : SuccessResponseDto
