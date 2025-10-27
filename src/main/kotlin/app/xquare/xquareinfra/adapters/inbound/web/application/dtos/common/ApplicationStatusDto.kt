package app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common

import app.xquare.xquareinfra.domain.application.ApplicationStatus
import com.fasterxml.jackson.annotation.JsonValue

enum class ApplicationStatusDto(
    @JsonValue val value: String,
) {
    PENDING("pending"),
    UNPUBLISHED("unpublished"),
    PUBLISHED("published"),
}

fun ApplicationStatus.toDto(): ApplicationStatusDto =
    when (this) {
        ApplicationStatus.PENDING -> ApplicationStatusDto.PENDING
        ApplicationStatus.UNPUBLISHED -> ApplicationStatusDto.UNPUBLISHED
        ApplicationStatus.PUBLISHED -> ApplicationStatusDto.PUBLISHED
    }

fun ApplicationStatusDto.toDomain(): ApplicationStatus =
    when (this) {
        ApplicationStatusDto.PENDING -> ApplicationStatus.PENDING
        ApplicationStatusDto.UNPUBLISHED -> ApplicationStatus.UNPUBLISHED
        ApplicationStatusDto.PUBLISHED -> ApplicationStatus.PUBLISHED
    }
