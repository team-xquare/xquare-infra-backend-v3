package app.xquare.xquareinfra.adapters.outbound.persistence.user.mappers

import app.xquare.xquareinfra.domain.user.UserRole
import app.xquare.xquareinfra.infrastructure.persistence.user.schema.UserPersistenceRole

fun UserRole.toPersistence(): UserPersistenceRole =
    when (this) {
        UserRole.ADMIN -> UserPersistenceRole.ADMIN
        UserRole.MEMBER -> UserPersistenceRole.MEMBER
    }

fun UserPersistenceRole.toDomain(): UserRole =
    when (this) {
        UserPersistenceRole.ADMIN -> UserRole.ADMIN
        UserPersistenceRole.MEMBER -> UserRole.MEMBER
    }
