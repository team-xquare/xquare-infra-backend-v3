package app.xquare.xquareinfra.application.accountRecovery.ports.inbound

interface SendPasswordResetOtpUseCase {
    fun sendPasswordResetOtp(command: SendPasswordResetOtpCommand)
}

data class SendPasswordResetOtpCommand(
    val username: String,
    val studentNumber: Int,
    val name: String,
    val email: String,
)
