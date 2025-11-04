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
        getSecretData(application.name)
            .map { (key, value) -> EnvironmentVariable(application, key, value) }

    override fun setEnvironmentVariable(environmentVariable: EnvironmentVariable) {
        val data = getSecretData(environmentVariable.application.name).toMutableMap()
        data[environmentVariable.key] = environmentVariable.value
        setSecretData(environmentVariable.application.name, data)
    }

    override fun deleteEnvironmentVariable(
        application: Application,
        key: String,
    ) {
        val data = getSecretData(application.name).toMutableMap()
        data.remove(key)
        setSecretData(application.name, data)
    }

    private fun getSecretData(name: String): Map<String, String> {
        val response =
            vaultClient.getSecret(
                authorization = vaultProperties.token,
                mount = vaultProperties.mount,
                secret = name,
            )
        return response.data
    }

    private fun setSecretData(
        name: String,
        data: Map<String, String>,
    ) {
        vaultClient.setSecret(
            authorization = vaultProperties.token,
            mount = vaultProperties.mount,
            secret = name,
            data = data,
        )
    }
}
