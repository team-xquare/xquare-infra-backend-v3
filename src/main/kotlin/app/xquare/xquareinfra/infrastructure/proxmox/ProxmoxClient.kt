package app.xquare.xquareinfra.infrastructure.proxmox

import app.xquare.xquareinfra.infrastructure.proxmox.dtos.ProxmoxResponse
import app.xquare.xquareinfra.infrastructure.proxmox.dtos.ProxmoxVersion
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "proxmoxClient",
    url = "\${proxmox.url}",
    configuration = [ProxmoxFeignConfig::class],
)
interface ProxmoxClient {
    @GetMapping("/api2/json/version")
    fun getVersion(
        @RequestHeader("Authorization") authorization: String,
    ): ProxmoxResponse<ProxmoxVersion>
}
