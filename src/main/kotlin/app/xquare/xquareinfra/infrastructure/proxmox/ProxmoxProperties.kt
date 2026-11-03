package app.xquare.xquareinfra.infrastructure.proxmox

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "proxmox")
data class ProxmoxProperties(
    val url: String,
    val tokenId: String,
    val tokenSecret: String,
    val caCert: String = "",
)
