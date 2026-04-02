package app.xquare.xquareinfra.application.auth.ports.inbound

interface SendEmailOtpUseCase {
    fun sendOtp(command: SendEmailOtpCommand)
}

data class SendEmailOtpCommand(
    val email: String,
)
