package app.xquare.xquareinfra.infrastructure.github.dtos

data class ContentFileResponse(
    val type: String,
    val encoding: String,
    val size: Int,
    val name: String,
    val path: String,
    val content: String,
)
