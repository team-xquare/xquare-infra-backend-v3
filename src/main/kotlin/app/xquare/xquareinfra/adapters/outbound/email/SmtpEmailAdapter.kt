package app.xquare.xquareinfra.adapters.outbound.email

import app.xquare.xquareinfra.application.auth.ports.outbound.EmailSendPort
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class SmtpEmailAdapter(
    private val mailSender: JavaMailSender,
) : EmailSendPort {
    override fun send(to: String, subject: String, body: String) {
        val message = SimpleMailMessage().apply {
            setTo(to)
            setSubject(subject)
            setText(body)
        }
        mailSender.send(message)
    }
}