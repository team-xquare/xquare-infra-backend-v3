package app.xquare.xquareinfra.infrastructure.github

import app.xquare.xquareinfra.infrastructure.github.dtos.ContentFileResponse
import app.xquare.xquareinfra.infrastructure.github.dtos.RepositoryDispatchRequest
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "githubClient",
    url = "https://api.github.com",
)
interface GithubClient {
    @GetMapping("/repos/{owner}/{repo}/contents/{path}")
    @Headers("Accept: application/vnd.github.object+json")
    fun getRepositoryContent(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable owner: String,
        @PathVariable repo: String,
        @PathVariable path: String,
        @RequestParam("ref") branch: String,
    ): ResponseEntity<ContentFileResponse>

    @PostMapping("/repos/{owner}/{repo}/dispatches")
    @Headers("Accept: application/vnd.github+json")
    fun <T> sendRepositoryDispatch(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable owner: String,
        @PathVariable repo: String,
        @RequestBody payload: RepositoryDispatchRequest<T>,
    ): ResponseEntity<Void>
}
