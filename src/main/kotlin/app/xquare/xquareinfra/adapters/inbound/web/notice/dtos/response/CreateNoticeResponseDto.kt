package app.xquare.xquareinfra.adapters.inbound.web.notice.dtos.response

import app.xquare.xquareinfra.infrastructure.web.dto.SuccessResponseDto

data class CreateNoticeResponseDto(
    val noticeId: Long,
) : SuccessResponseDto
