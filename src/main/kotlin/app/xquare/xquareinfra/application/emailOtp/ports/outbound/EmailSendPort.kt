package app.xquare.xquareinfra.application.emailOtp.ports.outbound

interface EmailSendPort {
    fun send(
        to: String,
        subject: String,
        body: String,
    )

    fun sendWithTemplate(
        to: String,
        subject: String,
        templateName: String,
        variables: Map<String, Any>,
    )
}
