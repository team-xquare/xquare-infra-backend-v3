package app.xquare.xquareinfra.domain.notice

import app.xquare.xquareinfra.domain.user.User
import java.time.LocalDateTime

data class Notice(
    val id: Long? = null,
    val title: String,
    val content: String,
    val author: User,
    val fileUrl: String? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
