package app.xquare.xquareinfra.application.accountRecovery.ports.inbound

interface ResetPasswordUseCase {
    fun resetPassword(command: ResetPasswordCommand)
}

data class ResetPasswordCommand(
    val passwordResetToken: String,
    val newPassword: String,
)
