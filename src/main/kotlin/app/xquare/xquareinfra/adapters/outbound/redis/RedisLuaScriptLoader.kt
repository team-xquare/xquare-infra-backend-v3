package app.xquare.xquareinfra.adapters.outbound.redis

import org.springframework.core.io.ClassPathResource
import org.springframework.data.redis.core.script.DefaultRedisScript

internal object RedisLuaScriptLoader {
    fun <T> load(
        path: String,
        resultType: Class<T>,
    ): DefaultRedisScript<T> =
        DefaultRedisScript<T>().apply {
            setScriptText(
                ClassPathResource(path)
                    .inputStream
                    .bufferedReader()
                    .use { it.readText() },
            )
            this.resultType = resultType
        }
}
