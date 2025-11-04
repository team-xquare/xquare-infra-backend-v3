package app.xquare.xquareinfra.domain.environmentVariable

import app.xquare.xquareinfra.domain.application.Application

data class EnvironmentVariable(
    val application: Application,
    val key: String,
    val value: String,
)
