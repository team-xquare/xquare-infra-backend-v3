package app.xquare.xquareinfra.adapters.outbound.persistence.notice.mappers

import app.xquare.xquareinfra.adapters.outbound.persistence.user.mappers.toDomain
import app.xquare.xquareinfra.adapters.outbound.persistence.user.mappers.toPersistence
import app.xquare.xquareinfra.domain.notice.Notice
import app.xquare.xquareinfra.infrastructure.persistence.notice.schema.NoticePersistenceEntity

fun Notice.toPersistence(): NoticePersistenceEntity =
    NoticePersistenceEntity(
        id = this.id,
        title = this.title,
        content = this.content,
        author = this.author.toPersistence(),
        fileUrl = this.fileUrl,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

fun NoticePersistenceEntity.toDomain(): Notice =
    Notice(
        id = this.id,
        title = this.title,
        content = this.content,
        author = this.author.toDomain(),
        fileUrl = this.fileUrl,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )