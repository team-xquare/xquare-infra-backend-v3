package app.xquare.xquareinfra.application.accountRecovery.ports.inbound

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
