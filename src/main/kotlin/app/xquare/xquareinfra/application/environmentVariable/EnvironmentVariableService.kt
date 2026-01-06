package app.xquare.xquareinfra.application.environmentVariable

import app.xquare.xquareinfra.adapters.outbound.vault.VaultException
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.DeleteEnvironmentVariableCommand
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.DeleteEnvironmentVariableResult
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.DeleteEnvironmentVariableUseCase
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.EnvironmentVariableSummary
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.ListEnvironmentVariablesQuery
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.ListEnvironmentVariablesResult
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.ListEnvironmentVariablesUseCase
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.SetEnvironmentVariableCommand
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.SetEnvironmentVariableResult
import app.xquare.xquareinfra.application.environmentVariable.ports.inbound.SetEnvironmentVariableUseCase
import app.xquare.xquareinfra.application.environmentVariable.ports.outbound.ApplicationPersistenceForEnvironmentVariablePort
import app.xquare.xquareinfra.application.environmentVariable.ports.outbound.EnvironmentVariableVaultPort
import app.xquare.xquareinfra.application.global.exception.CommonException
import app.xquare.xquareinfra.domain.environmentVariable.EnvironmentVariable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EnvironmentVariableService(
    private val applicationPersistencePort: ApplicationPersistenceForEnvironmentVariablePort,
    private val environmentVariableVaultPort: EnvironmentVariableVaultPort,
) : SetEnvironmentVariableUseCase,
    ListEnvironmentVariablesUseCase,
    DeleteEnvironmentVariableUseCase {
    override fun setEnvironmentVariable(command: SetEnvironmentVariableCommand): SetEnvironmentVariableResult {
        val application =
            applicationPersistencePort.findById(command.applicationId)
                ?: throw CommonException.ApplicationNotFound

        if (!application.team.isMember(command.userId)) {
            throw CommonException.NotTeamMember
        }

        val environmentVariable =
            EnvironmentVariable(
                application = application,
                key = command.key,
                value = command.value,
            )

        environmentVariableVaultPort.setEnvironmentVariable(environmentVariable)

        return SetEnvironmentVariableResult
    }

    override fun listEnvironmentVariables(query: ListEnvironmentVariablesQuery): ListEnvironmentVariablesResult {
        val application =
            applicationPersistencePort.findById(query.applicationId)
                ?: throw CommonException.ApplicationNotFound

        if (!application.team.isMember(query.userId)) {
            throw CommonException.NotTeamMember
        }

        val environmentVariables = environmentVariableVaultPort.listEnvironmentVariables(application)
        val summaries = environmentVariables.map { EnvironmentVariableSummary(it.key) }

        return ListEnvironmentVariablesResult(environmentVariables = summaries)
    }

    override fun deleteEnvironmentVariable(command: DeleteEnvironmentVariableCommand): DeleteEnvironmentVariableResult {
        val application =
            applicationPersistencePort.findById(command.applicationId)
                ?: throw CommonException.ApplicationNotFound

        if (!application.team.isMember(command.userId)) {
            throw CommonException.NotTeamMember
        }

        try {
            environmentVariableVaultPort.deleteEnvironmentVariable(
                application = application,
                key = command.key,
            )
        } catch (e: VaultException.VaultSecretNotFound) {
            throw EnvironmentVariableException.VariableNotFound
        }

        return DeleteEnvironmentVariableResult
    }
}
