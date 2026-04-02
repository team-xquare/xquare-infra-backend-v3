package app.xquare.xquareinfra.application.auth.ports.outbound

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