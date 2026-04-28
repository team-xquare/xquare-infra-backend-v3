package app.xquare.xquareinfra.application.emailOtp

enum class EmailOtpPurpose(
    val key: String,
) {
    REGISTER("register"),
    USERNAME_RECOVERY("username-recovery"),
}
