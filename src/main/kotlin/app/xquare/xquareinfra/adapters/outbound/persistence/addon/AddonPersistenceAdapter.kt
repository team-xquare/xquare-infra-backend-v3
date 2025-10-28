package app.xquare.xquareinfra.adapters.outbound.persistence.addon

import app.xquare.xquareinfra.adapters.outbound.persistence.addon.mappers.toDomain
import app.xquare.xquareinfra.adapters.outbound.persistence.addon.mappers.toPersistence
import app.xquare.xquareinfra.application.addon.ports.outbound.AddonPersistenceForAddonPort
import app.xquare.xquareinfra.application.team.ports.outbound.AddonPersistenceForTeamPort
import app.xquare.xquareinfra.domain.addon.Addon
import app.xquare.xquareinfra.infrastructure.persistence.addon.repositories.AddonRepository
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class AddonPersistenceAdapter(
    private val addonRepository: AddonRepository,
) : AddonPersistenceForAddonPort,
    AddonPersistenceForTeamPort {
    override fun findById(addonId: Long): Addon? = addonRepository.findById(addonId).getOrNull()?.toDomain()

    override fun existsByNameAndTeamId(
        name: String,
        teamId: Long,
    ): Boolean = addonRepository.existsByNameAndTeamId(name, teamId)

    override fun save(addon: Addon): Addon {
        val entity = addon.toPersistence()
        val saved = addonRepository.save(entity)
        return saved.toDomain()
    }

    override fun delete(addonId: Long) = addonRepository.deleteById(addonId)

    override fun listByTeamId(teamId: Long): List<Addon> = addonRepository.findAllByTeamId(teamId).map { it.toDomain() }
}
