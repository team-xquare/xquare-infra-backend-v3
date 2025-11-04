package app.xquare.xquareinfra.adapters.inbound.web.addon.errorCode

import app.xquare.xquareinfra.infrastructure.web.ErrorCode

enum class AddonErrorCode(
    override val value: String,
) : ErrorCode {
    ADDON_ALREADY_EXISTS("ADDON_ALREADY_EXISTS"),
}
