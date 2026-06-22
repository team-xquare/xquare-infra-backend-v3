package app.xquare.xquareinfra.application.auth.ports.inbound.recovery

interface VerifyUsernameFindOtpUseCase {
    fun verifyUsernameFindOtp(command: VerifyUsernameFindOtpCommand): VerifyUsernameFindOtpResult
}

data class VerifyUsernameFindOtpCommand(
    val studentNumber: Int,
    val name: String,
    val email: String,
    val otp: String,
)

data class VerifyUsernameFindOtpResult(
    val username: String,
)
