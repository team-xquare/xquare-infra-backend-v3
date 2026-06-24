package app.xquare.xquareinfra.application.auth.ports.inbound.recovery

interface SendUsernameFindOtpUseCase {
    fun sendUsernameFindOtp(command: SendUsernameFindOtpCommand)
}

data class SendUsernameFindOtpCommand(
    val studentNumber: Int,
    val name: String,
    val email: String,
)
