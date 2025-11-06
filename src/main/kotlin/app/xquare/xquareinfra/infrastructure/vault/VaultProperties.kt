package app.xquare.xquareinfra.infrastructure.vault

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "vault")
data class VaultProperties(
    val token: String,
    val mount: String,
)
