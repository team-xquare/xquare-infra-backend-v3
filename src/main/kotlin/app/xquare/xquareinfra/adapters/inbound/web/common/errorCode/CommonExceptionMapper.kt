package app.xquare.xquareinfra.adapters.inbound.web.common.errorCode

import app.xquare.xquareinfra.application.global.exception.CommonException
import app.xquare.xquareinfra.infrastructure.web.toWrappedDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

object CommonExceptionMapper {
    fun toResponseEntity(ex: CommonException): ResponseEntity<Any> =
        when (ex) {
            is CommonException.UnAuthorized ->
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CommonErrorCode.UNAUTHORIZED.toWrappedDto())
            is CommonException.TeamNotFound ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonErrorCode.TEAM_NOT_FOUND.toWrappedDto())
            is CommonException.UserNotFound ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonErrorCode.USER_NOT_FOUND.toWrappedDto())
            is CommonException.NotTeamMember ->
                ResponseEntity.status(HttpStatus.FORBIDDEN).body(CommonErrorCode.NOT_TEAM_MEMBER.toWrappedDto())
            is CommonException.ApplicationNotFound ->
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(CommonErrorCode.APPLICATION_NOT_FOUND.toWrappedDto())
        }
}
