package app.xquare.xquareinfra.adapters.outbound.redis

import app.xquare.xquareinfra.application.emailOtp.EmailOtpPurpose
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
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
    fun `lua scripts are loaded from classpath resources`() {
        val consumeOtpScript = RedisLuaScriptLoader.load("redis/scripts/consume-otp.lua", Long::class.java)
        val consumeVerifiedTokenScript =
            RedisLuaScriptLoader.load("redis/scripts/consume-verified-token.lua", String::class.java)

        assertTrue(consumeOtpScript.scriptAsString.contains("local current = redis.call('GET', KEYS[1])"))
        assertTrue(consumeVerifiedTokenScript.scriptAsString.contains("if ARGV[1] ~= '' and current ~= ARGV[1] then"))
    }

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
