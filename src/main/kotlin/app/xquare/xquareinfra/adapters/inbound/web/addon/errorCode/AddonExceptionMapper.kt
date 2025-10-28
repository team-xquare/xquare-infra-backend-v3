package app.xquare.xquareinfra.adapters.inbound.web.addon.errorCode

import app.xquare.xquareinfra.application.addon.AddonException
import app.xquare.xquareinfra.infrastructure.web.toWrappedDto
import org.springframework.http.ResponseEntity

object AddonExceptionMapper {
    fun toResponseEntity(ex: AddonException): ResponseEntity<Any> =
        when (ex) {
            is AddonException.AddonNameAlreadyExists ->
                ResponseEntity.badRequest().body(AddonErrorCode.ADDON_ALREADY_EXISTS.toWrappedDto())
        }
}
