package app.xquare.xquareinfra.adapters.outbound.passwordEncoder

import app.xquare.xquareinfra.application.auth.ports.outbound.PasswordEncoderPort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordEncoderAdapter : PasswordEncoderPort {
    private val encoder = BCryptPasswordEncoder()

    override fun encode(password: String): String = encoder.encode(password)

    override fun matches(
        password: String,
        encodedPassword: String,
    ): Boolean = encoder.matches(password, encodedPassword)
}
