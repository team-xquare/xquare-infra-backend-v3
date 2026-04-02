package app.xquare.xquareinfra.application.auth.ports.inbound

interface SendEmailOtpUseCase {
    fun sendOtp(command: SendEmailOtpCommend)
}

data class SendEmailOtpCommend(
    val email: String,
)
