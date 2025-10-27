package app.xquare.xquareinfra.adapters.outbound.persistence.application.mappers

import app.xquare.xquareinfra.domain.application.ApplicationStatus
import app.xquare.xquareinfra.infrastructure.persistence.application.schema.ApplicationPersistenceStatus

fun ApplicationStatus.toPersistence(): ApplicationPersistenceStatus =
    when (this) {
        ApplicationStatus.PENDING -> ApplicationPersistenceStatus.PENDING
        ApplicationStatus.PUBLISHED -> ApplicationPersistenceStatus.PUBLISHED
        ApplicationStatus.UNPUBLISHED -> ApplicationPersistenceStatus.UNPUBLISHED
    }

fun ApplicationPersistenceStatus.toDomain(): ApplicationStatus =
    when (this) {
        ApplicationPersistenceStatus.PENDING -> ApplicationStatus.PENDING
        ApplicationPersistenceStatus.PUBLISHED -> ApplicationStatus.PUBLISHED
        ApplicationPersistenceStatus.UNPUBLISHED -> ApplicationStatus.UNPUBLISHED
    }
