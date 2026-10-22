package app.xquare.xquareinfra.application.auth.ports.inbound.recovery

interface VerifyPasswordResetOtpUseCase {
    fun verifyPasswordResetOtp(command: VerifyPasswordResetOtpCommand): VerifyPasswordResetOtpResult
}

data class VerifyPasswordResetOtpCommand(
    val username: String,
    val studentNumber: Int,
    val name: String,
    val email: String,
    val otp: String,
)

data class VerifyPasswordResetOtpResult(
    val passwordResetToken: String,
)
