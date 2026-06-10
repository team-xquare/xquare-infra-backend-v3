package app.xquare.xquareinfra.adapters.outbound.redis

import kotlin.test.Test
import kotlin.test.assertEquals
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
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
}
