package app.xquare.xquareinfra.adapters.inbound.web.team.errorCode

import app.xquare.xquareinfra.application.team.TeamException
import app.xquare.xquareinfra.infrastructure.web.toWrappedDto
import org.springframework.http.ResponseEntity

object TeamExceptionMapper {
    fun toResponseEntity(ex: TeamException): ResponseEntity<Any> =
        when (ex) {
            is TeamException.TeamNameAlreadyExists ->
                ResponseEntity.badRequest().body(TeamErrorCode.TEAM_ALREADY_EXISTS.toWrappedDto())
            is TeamException.NotTeamAdmin ->
                ResponseEntity.status(403).body(TeamErrorCode.INVALID_ACCESS.toWrappedDto())
        }
}
