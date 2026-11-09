package app.xquare.xquareinfra.infrastructure.proxmox.dtos

data class ProxmoxVersion(
    val version: String,
    val release: String?,
    val repoid: String?,
)
