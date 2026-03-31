package app.xquare.xquareinfra.application.auth

import app.xquare.xquareinfra.application.auth.ports.inbound.LoginCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.LoginResult
import app.xquare.xquareinfra.application.auth.ports.inbound.LoginUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.RefreshTokenCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.RefreshTokenResult
import app.xquare.xquareinfra.application.auth.ports.inbound.RefreshTokenUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterResult
import app.xquare.xquareinfra.application.auth.ports.inbound.RegisterUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.SendEmailOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.SendEmailOtpUseCase
import app.xquare.xquareinfra.application.auth.ports.inbound.VerifyEmailOtpCommand
import app.xquare.xquareinfra.application.auth.ports.inbound.VerifyEmailOtpResult
import app.xquare.xquareinfra.application.auth.ports.inbound.VerifyEmailOtpUseCase
import app.xquare.xquareinfra.application.auth.ports.outbound.AccessTokenPort
import app.xquare.xquareinfra.application.auth.ports.outbound.EmailOtpPort
import app.xquare.xquareinfra.application.auth.ports.outbound.EmailSendPort
import app.xquare.xquareinfra.application.auth.ports.outbound.PasswordEncoderPort
import app.xquare.xquareinfra.application.auth.ports.outbound.RefreshTokenPort
import app.xquare.xquareinfra.application.auth.ports.outbound.UserPersistenceForAuthPort
import app.xquare.xquareinfra.domain.user.User
import app.xquare.xquareinfra.domain.user.UserRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Transactional
@Service
class AuthService(
    private val userPersistencePort: UserPersistenceForAuthPort,
    private val accessTokenPort: AccessTokenPort,
    private val refreshTokenPort: RefreshTokenPort,
    private val passwordEncoderPort: PasswordEncoderPort,
    private val emailSendPort: EmailSendPort,
    private val emailOtpPort: EmailOtpPort,
) : RegisterUseCase,
    LoginUseCase,
    RefreshTokenUseCase,
    SendEmailOtpUseCase,
    VerifyEmailOtpUseCase {
    override fun register(command: RegisterCommand): RegisterResult {
        if (userPersistencePort.existsByUsername(command.username)) {
            throw AuthException.UsernameAlreadyExists
        }

        val encodedPassword = passwordEncoderPort.encode(command.password)
        val user =
            User(
                username = command.username,
                password = encodedPassword,
                role = UserRole.MEMBER,
                studentNumber = command.studentNumber,
                name = command.name,
                email = command.email,
            )

        val savedUser = userPersistencePort.save(user)
        emailOtpPort.deleteVerifiedToken(command.emailVerifiedToken)

        val accessToken = accessTokenPort.create(savedUser.id!!)
        val refreshToken = refreshTokenPort.create(savedUser.id)

        return RegisterResult(accessToken = accessToken, refreshToken = refreshToken)
    }

    override fun sendOtp(command: SendEmailOtpCommand) {
        val otp = (100000..999999).random().toString()
        emailOtpPort.saveOtp(command.email, otp, ttlSeconds = 300)

        val htmlBody = """
        <!DOCTYPE html>
        <html lang="ko">
        <body style="margin:0;padding:0;background:#f4f4f4;font-family:Arial,sans-serif;">
          <table width="100%" cellpadding="0" cellspacing="0">
            <tr>
              <td align="center" style="padding:40px 0;">
                <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:8px;overflow:hidden;">
                  
                  <tr>
                    <td style="padding:12px 32px;background:#f8f8f8;font-size:13px;color:#999;">
                      Xquare
                    </td>
                  </tr>
                  
                  <tr>
                    <td style="padding:40px 32px 24px;">
                      <h1 style="margin:0 0 24px;font-size:24px;font-weight:700;color:#111;">
                        이메일 인증 코드 안내
                      </h1>
                      <p style="margin:0 0 8px;font-size:15px;color:#333;">안녕하세요.</p>
                      <p style="margin:0 0 32px;font-size:15px;color:#333;">
                        회원가입을 완료하려면 아래 인증 코드를 입력해주세요.
                      </p>
                      
                      <div style="background:#f0f0f0;border-radius:8px;padding:28px;text-align:center;margin-bottom:32px;">
                        <span style="font-size:36px;font-weight:700;letter-spacing:8px;color:#111;">
                          $otp
                        </span>
                      </div>
                      
                      <p style="margin:0 0 8px;font-size:14px;color:#666;">
                        인증 코드는 <strong>5분</strong> 동안 유효합니다.
                      </p>
                      <p style="margin:0;font-size:14px;color:#666;">
                        본인이 요청하지 않았다면 이 이메일은 무시하셔도 됩니다.
                      </p>
                    </td>
                  </tr>
                  
                  <tr>
                    <td style="padding:20px 32px;background:#f8f8f8;border-top:1px solid #eee;">
                      <p style="margin:0;font-size:12px;color:#aaa;">
                        문의: abeua8684@gmail.com<br>
                        © 2026 Xquare
                      </p>
                    </td>
                  </tr>
                  
                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
    """.trimIndent()

        emailSendPort.send(
            to = command.email,
            subject = "[Xquare] 이메일 인증 코드",
            body = htmlBody,
        )
    }
    override fun verifyOtp(commend: VerifyEmailOtpCommand): VerifyEmailOtpResult {
        val savedOtp =
            emailOtpPort.getOtp(commend.email)
                ?: throw AuthException.OtpNotFound

        if (savedOtp != commend.otp) {
            throw AuthException.OtpMismatch
        }

        emailOtpPort.deleteOtp(commend.email)

        val verifiedToken = UUID.randomUUID().toString()
        emailOtpPort.saveVerifiedToken(verifiedToken, commend.email, ttlSeconds = 600)

        return VerifyEmailOtpResult(emailVerifiedToken = verifiedToken)
    }

    override fun login(command: LoginCommand): LoginResult {
        val user =
            userPersistencePort.findByUsername(command.username)
                ?: throw AuthException.InvalidCredentials

        if (!passwordEncoderPort.matches(command.password, user.password)) {
            throw AuthException.InvalidCredentials
        }

        val accessToken = accessTokenPort.create(user.id!!)
        val refreshToken = refreshTokenPort.create(user.id)

        return LoginResult(accessToken = accessToken, refreshToken = refreshToken)
    }

    override fun refreshToken(command: RefreshTokenCommand): RefreshTokenResult {
        if (!refreshTokenPort.isValid(command.refreshToken)) {
            throw AuthException.InvalidRefreshToken
        }

        val userId =
            refreshTokenPort.extractUserId(command.refreshToken)
                ?: throw AuthException.InvalidRefreshToken

        val newAccessToken = accessTokenPort.create(userId)
        val newRefreshToken = refreshTokenPort.create(userId)

        return RefreshTokenResult(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
        )
    }
}
