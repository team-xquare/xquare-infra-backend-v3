package app.xquare.xquareinfra.application.auth.ports.inbound.recovery

interface ResetPasswordUseCase {
    fun resetPassword(command: ResetPasswordCommand)
}

data class ResetPasswordCommand(
    val passwordResetToken: String,
    val newPassword: String,
)
