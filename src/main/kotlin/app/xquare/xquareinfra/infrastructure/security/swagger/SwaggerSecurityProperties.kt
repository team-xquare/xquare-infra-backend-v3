package app.xquare.xquareinfra.infrastructure.security.swagger

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "swagger")
data class SwaggerSecurityProperties(
    val user: String,
    val password: String,
)
