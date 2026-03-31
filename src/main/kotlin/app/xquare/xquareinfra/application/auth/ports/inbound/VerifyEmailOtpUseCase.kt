package app.xquare.xquareinfra.application.auth.ports.inbound

interface VerifyEmailOtpUseCase {
    fun verifyOtp(command: VerifyEmailOtpCommand): VerifyEmailOtpResult
}

data class VerifyEmailOtpCommand(
    val email: String,
    val otp: String,
)

data class VerifyEmailOtpResult(
    val emailVerifiedToken: String,
)
