package app.xquare.xquareinfra.adapters.inbound.web.github

import app.xquare.xquareinfra.adapters.inbound.web.github.dtos.request.CreateGithubTokenRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.github.dtos.response.CreateGithubTokenResponseDto
import app.xquare.xquareinfra.infrastructure.github.GithubApplicationProperties
import app.xquare.xquareinfra.infrastructure.github.GithubOAuthClient
import app.xquare.xquareinfra.infrastructure.web.dto.APiWrappedResponseDto
import app.xquare.xquareinfra.infrastructure.web.dto.toWrappedDto
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "GitHub")
@RestController
@RequestMapping("/api/v1/github")
class GithubController(
    private val githubOAuthClient: GithubOAuthClient,
    private val githubProperties: GithubApplicationProperties,
) {
    @PostMapping("/token")
    fun createGithubToken(
        @RequestBody request: CreateGithubTokenRequestDto,
    ): APiWrappedResponseDto<CreateGithubTokenResponseDto> {
        val githubResponse =
            githubOAuthClient.exchangeCodeForToken(
                clientId = githubProperties.clientId,
                clientSecret = githubProperties.clientSecret,
                code = request.code,
                redirectUri = githubProperties.redirectUri,
            )
        return CreateGithubTokenResponseDto(
            accessToken = githubResponse.accessToken,
            tokenType = githubResponse.tokenType,
            scope = githubResponse.scope,
            expiresIn = githubResponse.expiresIn,
            refreshToken = githubResponse.refreshToken,
            refreshTokenExpiresIn = githubResponse.refreshTokenExpiresIn,
        ).toWrappedDto()
    }
}
