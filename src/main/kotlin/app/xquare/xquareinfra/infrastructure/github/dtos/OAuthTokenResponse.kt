package app.xquare.xquareinfra.infrastructure.github.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class OAuthTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("scope")
    val scope: String? = null,
    @JsonProperty("expires_in")
    val expiresIn: Long? = null,
    @JsonProperty("refresh_token")
    val refreshToken: String? = null,
    @JsonProperty("refresh_token_expires_in")
    val refreshTokenExpiresIn: Long? = null,
)
