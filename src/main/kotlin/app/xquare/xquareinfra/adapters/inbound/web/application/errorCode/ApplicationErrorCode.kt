package app.xquare.xquareinfra.adapters.inbound.web.application.errorCode

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class ApplicationErrorCode(
    override val value: String,
) : ErrorCode {
    APPLICATION_ALREADY_EXISTS("APPLICATION_ALREADY_EXISTS"),

    CONFIGURATION_FETCH_FAILED("CONFIGURATION_FETCH_FAILED"),

    INVALID_STATUS("INVALID_STATUS"),
}
