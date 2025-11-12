package app.xquare.xquareinfra.adapters.outbound.vault

import app.xquare.xquareinfra.application.environmentVariable.ports.outbound.EnvironmentVariableVaultPort
import app.xquare.xquareinfra.domain.application.Application
import app.xquare.xquareinfra.domain.environmentVariable.EnvironmentVariable
import app.xquare.xquareinfra.infrastructure.vault.VaultClient
import app.xquare.xquareinfra.infrastructure.vault.VaultProperties
import org.springframework.stereotype.Component

@Component
class VaultAdapter(
    private val vaultClient: VaultClient,
    private val vaultProperties: VaultProperties,
) : EnvironmentVariableVaultPort {
    override fun listEnvironmentVariables(application: Application): List<EnvironmentVariable> =
        getSecretData(application)
            .map { (key, value) -> EnvironmentVariable(application, key, value) }

    override fun setEnvironmentVariable(environmentVariable: EnvironmentVariable) {
        val data = getSecretData(environmentVariable.application).toMutableMap()
        data[environmentVariable.key] = environmentVariable.value
        setSecretData(environmentVariable.application, data)
    }

    override fun deleteEnvironmentVariable(
        application: Application,
        key: String,
    ) {
        val data = getSecretData(application).toMutableMap()
        data.remove(key)
        setSecretData(application, data)
    }

    private fun getSecretData(application: Application): Map<String, String> {
        val response =
            vaultClient.getSecret(
                authorization = vaultProperties.token,
                mount = vaultProperties.mount,
                secret = toSecretName(application),
            )
        return response.data
    }

    private fun setSecretData(
        application: Application,
        data: Map<String, String>,
    ) {
        vaultClient.setSecret(
            authorization = vaultProperties.token,
            mount = vaultProperties.mount,
            secret = toSecretName(application),
            data = data,
        )
    }

    private fun toSecretName(application: Application): String = "${application.team.name}-${application.name}"
}
