package app.xquare.xquareinfra.adapters.inbound.web.team.errorCode

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class TeamErrorCode(
    override val value: String,
) : ErrorCode {
    TEAM_ALREADY_EXISTS("TEAM_ALREADY_EXISTS"),
    INVALID_ACCESS("TEAM_INVALID_ACCESS"),
}