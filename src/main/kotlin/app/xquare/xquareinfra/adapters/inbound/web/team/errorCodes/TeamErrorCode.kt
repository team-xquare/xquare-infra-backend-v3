package app.xquare.xquareinfra.adapters.inbound.web.team.errorCodes

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class TeamErrorCode(
    override val value: String,
) : ErrorCode {
    TEAM_NOT_FOUND("TEAM_NOT_FOUND"),
    TEAM_ALREADY_EXISTS("TEAM_ALREADY_EXISTS"),
    INVALID_MEMBER("INVALID_MEMBER"),
    INVALID_ACCESS("INVALID_ACCESS"),
}
