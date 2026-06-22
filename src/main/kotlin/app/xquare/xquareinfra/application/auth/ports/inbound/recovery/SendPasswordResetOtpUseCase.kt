package app.xquare.xquareinfra.application.auth.ports.inbound.recovery

interface SendPasswordResetOtpUseCase {
    fun sendPasswordResetOtp(command: SendPasswordResetOtpCommand)
}

data class SendPasswordResetOtpCommand(
    val username: String,
    val studentNumber: Int,
    val name: String,
    val email: String,
)
