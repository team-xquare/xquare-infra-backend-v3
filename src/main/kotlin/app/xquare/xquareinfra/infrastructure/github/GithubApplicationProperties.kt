package app.xquare.xquareinfra.infrastructure.github

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "github-app")
data class GithubApplicationProperties(
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
)
