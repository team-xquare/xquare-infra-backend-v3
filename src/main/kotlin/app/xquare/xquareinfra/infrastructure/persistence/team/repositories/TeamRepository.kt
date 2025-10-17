package app.xquare.xquareinfra.infrastructure.persistence.team.repositories

import app.xquare.xquareinfra.infrastructure.persistence.team.schema.TeamPersistenceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository : JpaRepository<TeamPersistenceEntity, Long> {
    fun existsByName(name: String): Boolean

    fun findAllByMembersUserId(userId: Long): List<TeamPersistenceEntity>
}
