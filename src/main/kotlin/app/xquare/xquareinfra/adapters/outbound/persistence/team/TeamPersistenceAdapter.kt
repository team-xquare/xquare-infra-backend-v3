package app.xquare.xquareinfra.adapters.outbound.persistence.team

import app.xquare.xquareinfra.adapters.outbound.persistence.team.mappers.toDomain
import app.xquare.xquareinfra.adapters.outbound.persistence.team.mappers.toPersistence
import app.xquare.xquareinfra.application.addon.ports.outbound.TeamPersistenceForAddonPort
import app.xquare.xquareinfra.application.application.ports.outbound.TeamPersistenceForApplicationPort
import app.xquare.xquareinfra.application.team.ports.outbound.TeamPersistenceForTeamPort
import app.xquare.xquareinfra.domain.team.Team
import app.xquare.xquareinfra.infrastructure.persistence.team.repositories.TeamRepository
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class TeamPersistenceAdapter(
    private val teamRepository: TeamRepository,
) : TeamPersistenceForTeamPort,
    TeamPersistenceForApplicationPort,
    TeamPersistenceForAddonPort {
    override fun existsByName(name: String): Boolean = teamRepository.existsByName(name)

    override fun listAll(): List<Team> = teamRepository.findAll().map { it.toDomain() }

    override fun listByUserId(userId: Long): List<Team> = teamRepository.findAllByMembersUserId(userId).map { it.toDomain() }

    override fun findById(teamId: Long): Team? = teamRepository.findById(teamId).getOrNull()?.toDomain()

    override fun save(team: Team): Team {
        val entity = team.toPersistence()
        val saved = teamRepository.save(entity)
        return saved.toDomain()
    }

    override fun delete(teamId: Long) {
        teamRepository.deleteById(teamId)
    }
}
