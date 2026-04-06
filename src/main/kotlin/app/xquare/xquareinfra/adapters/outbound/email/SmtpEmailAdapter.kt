package app.xquare.xquareinfra.adapters.outbound.email

import app.xquare.xquareinfra.application.auth.ports.outbound.EmailSendPort
import app.xquare.xquareinfra.infrastructure.corutine.ApplicationCoroutineScope
import kotlinx.coroutines.launch
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Component
class SmtpEmailAdapter(
    private val mailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    private val applicationCoroutineScope: ApplicationCoroutineScope,
) : EmailSendPort {

    override fun send(
        to: String,
        subject: String,
        body: String,
    ) {
        applicationCoroutineScope.launch {
            val message = mailSender.createMimeMessage()
            MimeMessageHelper(message, true, "UTF-8").apply {
                setTo(to)
                setSubject(subject)
                setText(body, true)
            }
            mailSender.send(message)
        }
    }

    override fun sendWithTemplate(
        to: String,
        subject: String,
        templateName: String,
        variables: Map<String, Any>,
    ) {
        val context =Context().apply {setVariables(variables)}
        val htmlBody = templateEngine.process(templateName, context)
        send(to, subject, htmlBody)
    }
}