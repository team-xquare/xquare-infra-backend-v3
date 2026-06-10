package app.xquare.xquareinfra.adapters.outbound.redis

import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.core.script.DefaultRedisScript
import java.util.concurrent.TimeUnit

class RedisEmailOtpAdapterTest {
    @Test
    fun `legacy verified token operations use the same key`() {
        val redisTemplate = mock(StringRedisTemplate::class.java)
        @Suppress("UNCHECKED_CAST")
        val valueOperations = mock(ValueOperations::class.java) as ValueOperations<String, String>
        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)
        `when`(valueOperations.get("verifiedTokenverified-token")).thenReturn("user@test.com")

        val adapter = RedisEmailOtpAdapter(redisTemplate)

        adapter.saveVerifiedToken(
            token = "verified-token",
            email = "user@test.com",
            ttlSeconds = 600,
        )
        assertEquals("user@test.com", adapter.getEmailByVerifiedToken("verified-token"))
        adapter.deleteVerifiedToken("verified-token")

        verify(valueOperations).set("verifiedTokenverified-token", "user@test.com", 600, TimeUnit.SECONDS)
        verify(valueOperations).get("verifiedTokenverified-token")
        verify(redisTemplate).delete("verifiedTokenverified-token")
    }

    @Test
    fun `register verified token normalizes email on save and consume`() {
        val redisTemplate = mock(StringRedisTemplate::class.java)
        @Suppress("UNCHECKED_CAST")
        val valueOperations = mock(ValueOperations::class.java) as ValueOperations<String, String>
        `when`(redisTemplate.opsForValue()).thenReturn(valueOperations)

        var consumedExpectedEmail: String? = null
        `when`(
            redisTemplate.execute(
                anyObject<DefaultRedisScript<String>>(),
                eq(listOf("verified:register:verified-token")),
                anyObject<String>(),
            ),
        ).thenAnswer {
            consumedExpectedEmail = it.getArgument<String>(2)
            "user@test.com"
        }

        val adapter = RedisEmailOtpAdapter(redisTemplate)

        adapter.saveVerifiedToken(
            purpose = EmailOtpPurpose.REGISTER,
            token = "verified-token",
            email = "  User@Test.com ",
            ttlSeconds = 600,
        )
        val consumedEmail =
            adapter.consumeVerifiedToken(
                purpose = EmailOtpPurpose.REGISTER,
                token = "verified-token",
                expectedEmail = " user@test.com ",
            )

        assertEquals("user@test.com", consumedEmail)
        assertNotNull(consumedExpectedEmail)
        assertEquals("user@test.com", consumedExpectedEmail)
        verify(valueOperations).set("verified:register:verified-token", "user@test.com", 600, TimeUnit.SECONDS)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> anyObject(): T {
        org.mockito.ArgumentMatchers.any<T>()
        return null as T
    }
}
