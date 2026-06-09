package app.xquare.xquareinfra.application.accountRecovery.ports.inbound

interface SendUsernameFindOtpUseCase {
    fun sendUsernameFindOtp(command: SendUsernameFindOtpCommand)
}

data class SendUsernameFindOtpCommand(
    val studentNumber: Int,
    val name: String,
    val email: String,
)
