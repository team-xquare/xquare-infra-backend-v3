package app.xquare.xquareinfra.infrastructure.okhttp

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "okhttp")
data class OkHttpProperties(
    val cacheDir: String,
    val cacheSizeMb: Long,
)
