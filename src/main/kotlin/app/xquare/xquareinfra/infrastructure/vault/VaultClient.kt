package app.xquare.xquareinfra.infrastructure.vault

import app.xquare.xquareinfra.infrastructure.vault.dto.GetSecretResponseDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "vaultClient",
    url = "\${vault.url}",
)
interface VaultClient {
    @GetMapping("/v1/{mount}/{secret}")
    fun getSecret(
        @RequestHeader("X-Vault-Token") authorization: String,
        @PathVariable mount: String,
        @PathVariable secret: String,
    ): GetSecretResponseDto

    @PostMapping("/v1/{mount}/{secret}")
    fun setSecret(
        @RequestHeader("X-Vault-Token") authorization: String,
        @PathVariable mount: String,
        @PathVariable secret: String,
        @RequestBody data: Map<String, String>,
    )

    @DeleteMapping("/v1/{mount}/{secret}")
    fun deleteSecret(
        @RequestHeader("X-Vault-Token") authorization: String,
        @PathVariable mount: String,
        @PathVariable secret: String,
    )
}
