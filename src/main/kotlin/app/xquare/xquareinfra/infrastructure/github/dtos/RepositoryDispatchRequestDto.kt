package app.xquare.xquareinfra.infrastructure.github.dtos

data class RepositoryDispatchRequestDto(
    val event_type: String,
    val client_payload: Map<String, Any>,
)
