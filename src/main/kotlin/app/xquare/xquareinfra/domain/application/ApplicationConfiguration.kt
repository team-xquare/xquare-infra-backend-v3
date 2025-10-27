package app.xquare.xquareinfra.domain.application

enum class ApplicationTier {
    NANO,
    MICRO,
    SMALL,
    MEDIUM,
    LARGE,
}

data class ApplicationEndpoint(
    val port: Int,
    val routes: List<String>,
)

data class ApplicationGithubConfiguration(
    val owner: String,
    val repo: String,
    val branch: String,
    val installationId: String,
    val hash: String,
    val triggerPaths: List<String>? = null,
)

data class ApplicationConfiguration(
    val tier: ApplicationTier,
    val github: ApplicationGithubConfiguration,
    val build: BuildConfiguration,
    val endpoints: List<ApplicationEndpoint>,
)
