package app.xquare.xquareinfra.infrastructure.feign

import feign.Feign
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FeignConfig {
    @Bean
    fun feignBuilder(okHttpClient: OkHttpClient): Feign.Builder = Feign.builder().client(feign.okhttp.OkHttpClient(okHttpClient))
}
