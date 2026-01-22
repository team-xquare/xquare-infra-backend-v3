package app.xquare.xquareinfra.domain.application

data class ApplicationEndpoint(
    val port: Int,
    val routes: List<String>,
)

data class ApplicationGithubConfiguration(
    val owner: String,
    val repo: String,
    val branch: String,
    val installationId: String,
    val hash: String?,
    val triggerPaths: List<String>? = null,
)

data class ApplicationConfiguration(
    val github: ApplicationGithubConfiguration,
    val build: BuildConfiguration,
    val endpoints: List<ApplicationEndpoint>,
)
