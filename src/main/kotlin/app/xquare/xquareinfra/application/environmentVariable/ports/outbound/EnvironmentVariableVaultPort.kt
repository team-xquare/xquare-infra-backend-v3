package app.xquare.xquareinfra.application.environmentVariable.ports.outbound

import app.xquare.xquareinfra.domain.application.Application
import app.xquare.xquareinfra.domain.environmentVariable.EnvironmentVariable

interface EnvironmentVariableVaultPort {
    fun listEnvironmentVariables(application: Application): List<EnvironmentVariable>

    fun setEnvironmentVariable(environmentVariable: EnvironmentVariable)

    fun deleteEnvironmentVariable(
        application: Application,
        key: String,
    )
}
