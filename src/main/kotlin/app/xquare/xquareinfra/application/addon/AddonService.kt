package app.xquare.xquareinfra.application.addon

import app.xquare.xquareinfra.application.addon.ports.inbound.CreateAddonCommand
import app.xquare.xquareinfra.application.addon.ports.inbound.CreateAddonResult
import app.xquare.xquareinfra.application.addon.ports.inbound.CreateAddonUseCase
import app.xquare.xquareinfra.application.addon.ports.inbound.DeleteAddonCommand
import app.xquare.xquareinfra.application.addon.ports.inbound.DeleteAddonResult
import app.xquare.xquareinfra.application.addon.ports.inbound.DeleteAddonUseCase
import app.xquare.xquareinfra.application.addon.ports.inbound.GetAddonQuery
import app.xquare.xquareinfra.application.addon.ports.inbound.GetAddonResult
import app.xquare.xquareinfra.application.addon.ports.inbound.GetAddonUseCase
import app.xquare.xquareinfra.application.addon.ports.inbound.UpdateAddonCommand
import app.xquare.xquareinfra.application.addon.ports.inbound.UpdateAddonResult
import app.xquare.xquareinfra.application.addon.ports.inbound.UpdateAddonUseCase
import app.xquare.xquareinfra.application.addon.ports.outbound.AddonPersistenceForAddonPort
import app.xquare.xquareinfra.application.addon.ports.outbound.AddonPublishPort
import app.xquare.xquareinfra.application.addon.ports.outbound.TeamPersistenceForAddonPort
import app.xquare.xquareinfra.application.global.exception.CommonException
import app.xquare.xquareinfra.domain.addon.Addon
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AddonService(
    private val teamPersistencePort: TeamPersistenceForAddonPort,
    private val addonPersistencePort: AddonPersistenceForAddonPort,
    private val addonPublishPort: AddonPublishPort,
) : CreateAddonUseCase,
    GetAddonUseCase,
    UpdateAddonUseCase,
    DeleteAddonUseCase {
    override fun createAddon(command: CreateAddonCommand): CreateAddonResult {
        val team =
            teamPersistencePort.findById(command.teamId)
                ?: throw CommonException.TeamNotFound

        if (!team.isMember(command.userId)) {
            throw CommonException.NotTeamMember
        }

        if (addonPersistencePort.existsByNameAndTeamId(command.name, command.teamId)) {
            throw AddonException.AddonNameAlreadyExists
        }

        val addon =
            Addon(
                name = command.name,
                type = command.type,
                storageGi = command.storageGi,
                team = team,
                configuration = command.configuration,
            )

        addonPublishPort.publishAddon(team.name, addon)
        val savedAddon = addonPersistencePort.save(addon)

        return CreateAddonResult(addonId = savedAddon.id!!)
    }

    override fun getAddon(query: GetAddonQuery): GetAddonResult {
        val addon =
            addonPersistencePort.findById(query.addonId)
                ?: throw CommonException.AddonNotFound

        if (!addon.team.isMember(query.userId)) {
            throw CommonException.NotTeamMember
        }

        return GetAddonResult(addon)
    }

    override fun updateAddon(command: UpdateAddonCommand): UpdateAddonResult {
        val addon =
            addonPersistencePort.findById(command.addonId)
                ?: throw CommonException.AddonNotFound

        if (!addon.team.isMember(command.userId)) {
            throw CommonException.NotTeamMember
        }

        val updatedAddon = addon.copy(storageGi = command.storageGi)

        addonPublishPort.publishAddon(addon.team.name, updatedAddon)
        addonPersistencePort.save(updatedAddon)

        return UpdateAddonResult
    }

    override fun deleteAddon(command: DeleteAddonCommand): DeleteAddonResult {
        val addon =
            addonPersistencePort.findById(command.addonId)
                ?: throw CommonException.AddonNotFound

        if (!addon.team.isMember(command.userId)) {
            throw CommonException.NotTeamMember
        }

        addonPublishPort.unpublishAddon(addon.team.name, addon)
        addonPersistencePort.delete(addon.id!!)

        return DeleteAddonResult
    }
}
