package app.xquare.xquareinfra.application.auth.ports.outbound

interface EmailOtpPort {
    fun saveOtp(email: String, otp: String, ttlSeconds: Long)
    fun getOtp(email: String): String?
    fun deleteOtp(email: String)

    fun saveVerifiedToken(token: String, email: String, ttlSeconds: Long)
    fun getEmailByVerifiedToken(token: String): String?
    fun deleteVerifiedToken(token: String)
}