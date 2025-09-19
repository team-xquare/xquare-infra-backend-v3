package app.xquare.xquareinfra.application.auth.ports.outbound

interface AccessTokenPort {
    fun create(userId: Long): String
}
