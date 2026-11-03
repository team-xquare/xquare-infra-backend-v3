package app.xquare.xquareinfra.infrastructure.proxmox.dtos

data class ProxmoxResponse<T>(
    val data: T,
)
