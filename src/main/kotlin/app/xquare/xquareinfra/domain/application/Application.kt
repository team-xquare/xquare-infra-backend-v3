package app.xquare.xquareinfra.domain.application

import app.xquare.xquareinfra.domain.team.Team

data class Application(
    val id: Long? = null,
    val team: Team,
    val name: String,
    val status: ApplicationStatus,
    val configuration: ApplicationConfiguration? = null,
)
