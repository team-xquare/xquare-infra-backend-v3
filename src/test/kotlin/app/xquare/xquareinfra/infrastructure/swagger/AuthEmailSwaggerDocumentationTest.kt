package app.xquare.xquareinfra.infrastructure.swagger

import app.xquare.xquareinfra.adapters.inbound.web.auth.AuthController
import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.EmailVerifiedTokenResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.SendOtpRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.dtos.VerifyOtpRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.AuthRecoveryController
import app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos.PasswordResetTokenResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos.ResetPasswordRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos.SendPasswordResetOtpRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos.SendUsernameFindOtpRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos.UsernameResponseDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos.VerifyPasswordResetOtpRequestDto
import app.xquare.xquareinfra.adapters.inbound.web.auth.recovery.dtos.VerifyUsernameFindOtpRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthEmailSwaggerDocumentationTest {
    @Test
    fun `email endpoints are grouped under email swagger tag`() {
        val emailEndpoints =
            mapOf(
                AuthController::class to listOf("sendEmailOtp", "verifyEmailOtp"),
                AuthRecoveryController::class to
                    listOf(
                        "sendUsernameFindOtp",
                        "verifyUsernameFindOtp",
                        "sendPasswordResetOtp",
                        "verifyPasswordResetOtp",
                    ),
            )

        emailEndpoints.forEach { (controller, methodNames) ->
            methodNames.forEach { methodName ->
                val operation =
                    controller.declaredMemberFunctions
                        .single { it.name == methodName }
                        .findAnnotation<Operation>()

                assertNotNull(operation, "$methodName must declare @Operation")
                assertTrue(
                    operation.tags.contains("Email"),
                    "$methodName must be exposed under the Email swagger tag",
                )
            }
        }
    }

    @Test
    fun `email request and response dto fields declare swagger schema descriptions`() {
        val documentedDtos =
            listOf(
                SendOtpRequestDto::class,
                VerifyOtpRequestDto::class,
                EmailVerifiedTokenResponseDto::class,
                SendUsernameFindOtpRequestDto::class,
                VerifyUsernameFindOtpRequestDto::class,
                UsernameResponseDto::class,
                SendPasswordResetOtpRequestDto::class,
                VerifyPasswordResetOtpRequestDto::class,
                PasswordResetTokenResponseDto::class,
                ResetPasswordRequestDto::class,
            )

        documentedDtos.forEach { dto ->
            dto.memberProperties.forEach { property ->
                val schema = property.javaField?.getAnnotation(Schema::class.java)
                val fieldName = "${dto.simpleName}.${property.name}"

                assertNotNull(schema, "$fieldName must declare @Schema")
                assertTrue(schema.description.isNotBlank(), "$fieldName must describe the field")
                assertTrue(schema.example.isNotBlank(), "$fieldName must provide an example")
            }
        }
    }
}
