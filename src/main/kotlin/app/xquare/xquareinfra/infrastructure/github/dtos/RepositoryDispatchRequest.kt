package app.xquare.xquareinfra.infrastructure.github.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class RepositoryDispatchRequest<T>(
    @JsonProperty("event_type")
    val eventType: String,
    @JsonProperty("client_payload")
    val clientPayload: T,
)
