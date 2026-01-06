package app.xquare.xquareinfra.infrastructure.github

import app.xquare.xquareinfra.infrastructure.github.dtos.OAuthTokenResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "githubOAuthClient",
    url = "https://github.com",
)
interface GithubOAuthClient {
    @PostMapping(
        value = ["/login/oauth/access_token"],
    )
    fun exchangeCodeForToken(
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String,
        @RequestParam("code") code: String,
        @RequestParam("redirect_uri", required = false) redirectUri: String?,
        @RequestHeader(HttpHeaders.ACCEPT) accept: String = MediaType.APPLICATION_JSON_VALUE,
    ): OAuthTokenResponse
}
