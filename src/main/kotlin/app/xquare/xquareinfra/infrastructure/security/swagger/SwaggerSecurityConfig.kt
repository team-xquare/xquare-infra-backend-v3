package app.xquare.xquareinfra.infrastructure.security.swagger

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SwaggerSecurityConfig(
    private val swaggerSecurityProperties: SwaggerSecurityProperties,
) {
    @Bean
    @Order(1)
    fun swaggerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**")
            .csrf { it.disable() }
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .httpBasic {}
        return http.build()
    }

    @Bean
    fun swaggerUserDetailsService(): UserDetailsService {
        val user =
            User
                .builder()
                .username(swaggerSecurityProperties.user)
                .password(passwordEncoder().encode(swaggerSecurityProperties.password))
                .roles("ADMIN")
                .build()
        return InMemoryUserDetailsManager(user)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
