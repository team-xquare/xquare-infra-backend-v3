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
            .orEmpty()
            .map { (key, value) -> EnvironmentVariable(application, key, value) }

    override fun setEnvironmentVariable(environmentVariable: EnvironmentVariable) {
        val data = getSecretData(environmentVariable.application)?.toMutableMap() ?: mutableMapOf()
        data[environmentVariable.key] = environmentVariable.value
        setSecretData(environmentVariable.application, data)
    }

    override fun deleteEnvironmentVariable(
        application: Application,
        key: String,
    ) {
        val data = getSecretData(application)?.toMutableMap() ?: mutableMapOf()
        if (!data.containsKey(key)) {
            throw VaultException.VaultSecretNotFound()
        }
        data.remove(key)
        setSecretData(application, data)
    }

    private fun getSecretData(application: Application): Map<String, String>? =
        runCatching {
            vaultClient
                .getSecret(
                    authorization = "Bearer ${vaultProperties.token}",
                    mount = vaultProperties.mount,
                    secret = toSecretName(application),
                ).data
        }.getOrNull()

    private fun setSecretData(
        application: Application,
        data: Map<String, String>,
    ) {
        if (data.isEmpty()) {
            vaultClient.deleteSecret(
                authorization = "Bearer ${vaultProperties.token}",
                mount = vaultProperties.mount,
                secret = toSecretName(application),
            )
        } else {
            vaultClient.setSecret(
                authorization = "Bearer ${vaultProperties.token}",
                mount = vaultProperties.mount,
                secret = toSecretName(application),
                data = data,
            )
        }
    }

    private fun toSecretName(application: Application): String = "${application.team.name}-${application.name}"
}
