package app.xquare.xquareinfra.adapters.inbound.web.environmentVariable.errorCode

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class EnvironmentVariableErrorCode(
    override val value: String,
) : ErrorCode {
    VARIABLE_NOT_FOUND("VARIABLE_NOT_FOUND"),
}
