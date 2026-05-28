package app.xquare.xquareinfra.infrastructure.security.context

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository

@Configuration
class SecurityContextConfig {

    @Bean
    fun securityContextRepository(): SecurityContextRepository =
        RequestAttributeSecurityContextRepository()
}