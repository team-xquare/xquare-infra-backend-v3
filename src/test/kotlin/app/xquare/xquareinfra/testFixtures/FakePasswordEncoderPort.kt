package app.xquare.xquareinfra.testFixtures

import app.xquare.xquareinfra.application.auth.ports.outbound.PasswordEncoderPort

class FakePasswordEncoderPort : PasswordEncoderPort {
    override fun encode(password: String): String = "encoded:$password"

    override fun matches(
        password: String,
        encodedPassword: String,
    ): Boolean = encodedPassword == "encoded:$password"
}
