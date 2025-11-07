package app.xquare.xquareinfra.adapters.inbound.web.notice.dtos.response

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto
import java.time.LocalDateTime

data class NoticeSummaryResponseDto(
    val id: Long,
    val title: String,
    val author: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

data class ListNoticesResponseDto(
    val notices: List<NoticeSummaryResponseDto>,
) : SuccessResponseDto
