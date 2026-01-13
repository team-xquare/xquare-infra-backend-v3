package app.xquare.xquareinfra.infrastructure.okhttp

import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File

@Configuration
class OkHttpConfig(
    private val okHttpProperties: OkHttpProperties,
) {
    @Bean
    fun okHttpClient(): OkHttpClient {
        val cacheSize = okHttpProperties.cacheSizeMb * 1024 * 1024
        val cache = Cache(File(okHttpProperties.cacheDir), cacheSize)

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient
            .Builder()
            .cache(cache)
            .addInterceptor(logging)
            .build()
    }
}
