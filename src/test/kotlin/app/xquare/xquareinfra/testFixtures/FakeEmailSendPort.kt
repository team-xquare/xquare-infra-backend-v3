package app.xquare.xquareinfra.testFixtures

import app.xquare.xquareinfra.application.emailOtp.ports.outbound.EmailSendPort

class FakeEmailSendPort : EmailSendPort {
    val sentEmails = mutableListOf<SentEmail>()

    override fun send(
        to: String,
        subject: String,
        body: String,
    ) {
        sentEmails += SentEmail(to = to, subject = subject, body = body)
    }

    override fun sendWithTemplate(
        to: String,
        subject: String,
        templateName: String,
        variables: Map<String, Any>,
    ) {
        sentEmails += SentEmail(to = to, subject = subject, templateName = templateName, variables = variables)
    }
}

data class SentEmail(
    val to: String,
    val subject: String,
    val body: String? = null,
    val templateName: String? = null,
    val variables: Map<String, Any> = emptyMap(),
)
