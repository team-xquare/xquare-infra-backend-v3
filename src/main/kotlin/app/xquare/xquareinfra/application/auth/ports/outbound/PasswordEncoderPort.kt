package app.xquare.xquareinfra.application.auth.ports.outbound

interface PasswordEncoderPort {
    fun encode(password: String): String

    fun matches(
        password: String,
        encodedPassword: String,
    ): Boolean
}
