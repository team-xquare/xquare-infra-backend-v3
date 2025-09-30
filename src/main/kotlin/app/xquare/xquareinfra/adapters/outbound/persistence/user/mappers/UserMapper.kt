package app.xquare.xquareinfra.adapters.outbound.persistence.user.mappers

import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.infrastructure.persistence.user.schema.UserPersistenceEntity

fun User.toPersistence(): UserPersistenceEntity =
    UserPersistenceEntity(
        id = this.id,
        username = this.username,
        password = this.password,
        role = this.role.toPersistence(),
        studentNumber = this.studentNumber,
        name = this.name,
        email = this.email,
    )

fun UserPersistenceEntity.toDomain(): User =
    User(
        id = this.id,
        username = this.username,
        password = this.password,
        role = this.role.toDomain(),
        studentNumber = this.studentNumber,
        name = this.name,
        email = this.email,
    )
