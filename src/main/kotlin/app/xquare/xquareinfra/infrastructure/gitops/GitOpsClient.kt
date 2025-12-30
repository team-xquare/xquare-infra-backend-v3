package app.xquare.xquareinfra.infrastructure.gitops

import app.xquare.xquareinfra.infrastructure.github.GithubClient
import app.xquare.xquareinfra.infrastructure.github.dtos.RepositoryDispatchRequest
import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsAddon
import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsApplication
import app.xquare.xquareinfra.infrastructure.gitops.manifest.GitOpsProject
import app.xquare.xquareinfra.infrastructure.gitops.payloads.GitOpsApplyAddonPayload
import app.xquare.xquareinfra.infrastructure.gitops.payloads.GitOpsApplyApplicationPayload
import app.xquare.xquareinfra.infrastructure.gitops.payloads.GitOpsRemovePayload
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.stereotype.Component
import java.util.*

@Component
class GitOpsClient(
    private val gitOpsProperties: GitOpsProperties,
    private val githubClient: GithubClient,
) {
    private val yamlMapper =
        ObjectMapper(
            YAMLFactory(),
        ).registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun getApplication(
        projectName: String,
        applicationName: String,
    ): GitOpsApplication? =
        runCatching {
            val content =
                githubClient
                    .getRepositoryContent(
                        authorization = "Bearer ${gitOpsProperties.token}",
                        owner = gitOpsProperties.owner,
                        repo = gitOpsProperties.repo,
                        path = "projects/$projectName.yaml",
                        branch = gitOpsProperties.branch,
                    ).body
                    ?.content
                    ?: return null

            val decoded = Base64.getDecoder().decode(content.replace("\n", ""))
            yamlMapper
                .readValue(decoded, GitOpsProject::class.java)
                .applications
                ?.find { application -> application.name == applicationName }
        }.getOrNull()

    fun applyApplication(
        projectName: String,
        applicationName: String,
        application: GitOpsApplication,
    ) = sendRepositoryDispatch(
        GitOpsApplyApplicationPayload(
            path = "projects/$projectName/applications/$applicationName",
            spec = application,
        ),
    )

    fun removeApplication(
        projectName: String,
        applicationName: String,
    ) = sendRepositoryDispatch(
        GitOpsRemovePayload(
            path = "projects/$projectName/applications/$applicationName",
        ),
    )

    fun applyAddon(
        projectName: String,
        addonName: String,
        addon: GitOpsAddon,
    ) = sendRepositoryDispatch(
        GitOpsApplyAddonPayload(
            path = "projects/$projectName/addons/$addonName",
            spec = addon,
        ),
    )

    fun removeAddon(
        projectName: String,
        addonName: String,
    ) = sendRepositoryDispatch(
        GitOpsRemovePayload(
            path = "projects/$projectName/addons/$addonName",
        ),
    )

    fun removeProject(projectName: String) =
        sendRepositoryDispatch(
            GitOpsRemovePayload(
                path = "projects/$projectName",
            ),
        )

    private fun <T> sendRepositoryDispatch(payload: T) {
        githubClient.sendRepositoryDispatch(
            authorization = "Bearer ${gitOpsProperties.token}",
            owner = gitOpsProperties.owner,
            repo = gitOpsProperties.repo,
            payload =
                RepositoryDispatchRequest(
                    eventType = "config-api",
                    clientPayload = payload,
                ),
        )
    }
}
