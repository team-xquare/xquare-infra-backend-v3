package app.xquare.xquareinfra.adapters.inbound.web.common.errorCode

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class CommonErrorCode(
    override val value: String,
) : ErrorCode {
    UNAUTHORIZED("COMMON_UNAUTHORIZED"),
    USER_NOT_FOUND("COMMON_USER_NOT_FOUND"),
    TEAM_NOT_FOUND("COMMON_TEAM_NOT_FOUND"),
    APPLICATION_NOT_FOUND("COMMON_APPLICATION_NOT_FOUND"),
    ADDON_NOT_FOUND("COMMON_ADDON_NOT_FOUND"),
    NOTICE_NOT_FOUND("COMMON_NOTICE_NOT_FOUND"),
    NOT_TEAM_MEMBER("COMMON_NOT_TEAM_MEMBER"),
}
