package app.xquare.xquareinfra.infrastructure.persistence.addon.repositories

import app.xquare.xquareinfra.infrastructure.persistence.addon.schema.AddonPersistenceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddonRepository : JpaRepository<AddonPersistenceEntity, Long> {
    fun existsByNameAndTeamId(
        name: String,
        teamId: Long,
    ): Boolean

    fun findAllByTeamId(teamId: Long): List<AddonPersistenceEntity>
}
