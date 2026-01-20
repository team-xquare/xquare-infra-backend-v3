package app.xquare.xquareinfra.infrastructure.persistence.application.repositories

import app.xquare.xquareinfra.infrastructure.persistence.application.schema.ApplicationPersistenceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ApplicationRepository : JpaRepository<ApplicationPersistenceEntity, Long> {
    fun existsByTeamIdAndName(teamId: Long, name: String): Boolean

    fun findAllByTeamId(teamId: Long): List<ApplicationPersistenceEntity>

    fun deleteAllByTeamId(teamId: Long)
}
