package app.xquare.xquareinfra.adapters.inbound.web.common.errorCode

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class CommonErrorCode(
    override val value: String,
) : ErrorCode {
    USER_NOT_FOUND("COMMON_USER_NOT_FOUND"),
    TEAM_NOT_FOUND("COMMON_TEAM_NOT_FOUND"),
    NOT_TEAM_MEMBER("COMMON_NOT_TEAM_MEMBER"),
}
