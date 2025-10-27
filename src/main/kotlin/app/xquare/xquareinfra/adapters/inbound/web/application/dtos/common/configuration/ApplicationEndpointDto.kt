package app.xquare.xquareinfra.adapters.inbound.web.application.dtos.common.configuration

import app.xquare.xquareinfra.domain.application.ApplicationEndpoint

data class ApplicationEndpointDto(
    val port: Int,
    val routes: List<String>,
)

fun ApplicationEndpointDto.toDomain(): ApplicationEndpoint =
    ApplicationEndpoint(
        port = port,
        routes = routes,
    )

fun ApplicationEndpoint.toDto(): ApplicationEndpointDto =
    ApplicationEndpointDto(
        port = port,
        routes = routes,
    )
