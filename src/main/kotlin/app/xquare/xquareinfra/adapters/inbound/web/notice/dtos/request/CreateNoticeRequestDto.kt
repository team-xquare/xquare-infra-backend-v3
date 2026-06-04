package app.xquare.xquareinfra.adapters.inbound.web.notice.dtos.request

import org.springframework.web.multipart.MultipartFile

data class CreateNoticeRequestDto(
    val title: String,
    val content: String,
    val file: MultipartFile? = null,
)
