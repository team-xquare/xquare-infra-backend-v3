package app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos

import jakarta.validation.Validation
import kotlin.test.Test
import kotlin.test.assertTrue

class AuthRecoveryRequestDtoValidationTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `send password reset otp rejects invalid username characters`() {
        val request =
            SendPasswordResetOtpRequestDto(
                username = "test[user",
                studentNumber = 1101,
                name = "테스터",
                email = "user@test.com",
            )

        assertTrue(validator.validate(request).any { it.propertyPath.toString() == "username" })
    }

    @Test
    fun `username recovery rejects blank identifiers`() {
        val sendRequest =
            SendUsernameFindOtpRequestDto(
                studentNumber = 1101,
                name = "",
                email = "",
            )
        val verifyRequest =
            VerifyUsernameFindOtpRequestDto(
                studentNumber = 1101,
                name = "",
                email = "",
                otp = "123456",
            )

        assertTrue(validator.validate(sendRequest).any { it.propertyPath.toString() == "name" })
        assertTrue(validator.validate(sendRequest).any { it.propertyPath.toString() == "email" })
        assertTrue(validator.validate(verifyRequest).any { it.propertyPath.toString() == "name" })
        assertTrue(validator.validate(verifyRequest).any { it.propertyPath.toString() == "email" })
    }

    @Test
    fun `username recovery accepts Korean and English names with spaces or hyphens`() {
        val validNames = listOf("김 민수", "김-민수", "John Doe", "Anne-Marie")

        validNames.forEach { name ->
            val sendRequest =
                SendUsernameFindOtpRequestDto(
                    studentNumber = 1101,
                    name = name,
                    email = "user@test.com",
                )
            val verifyRequest =
                VerifyUsernameFindOtpRequestDto(
                    studentNumber = 1101,
                    name = name,
                    email = "user@test.com",
                    otp = "123456",
                )

            assertTrue(validator.validate(sendRequest).none { it.propertyPath.toString() == "name" })
            assertTrue(validator.validate(verifyRequest).none { it.propertyPath.toString() == "name" })
        }
    }

    @Test
    fun `password reset verification requires numeric otp`() {
        val request =
            VerifyPasswordResetOtpRequestDto(
                username = "tester",
                studentNumber = 1101,
                name = "테스터",
                email = "user@test.com",
                otp = "abcdef",
            )

        assertTrue(validator.validate(request).any { it.propertyPath.toString() == "otp" })
    }
}
