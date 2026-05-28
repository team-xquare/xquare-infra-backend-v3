package app.xquare.xquareinfra.infrastructure.security.api

import app.xquare.xquareinfra.application.user.ports.inbound.GetUserQuery
import app.xquare.xquareinfra.application.user.ports.inbound.GetUserUseCase
import app.xquare.xquareinfra.infrastructure.jwt.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val getUserUseCase: GetUserUseCase,
    private val jwtProvider: JwtProvider,
    private val securityContextRepository: SecurityContextRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val header = request.getHeader("Authorization")
            if (header.isNullOrEmpty() || !header.startsWith("Bearer ")) {
                return
            }

            val token = header.substring(7)
            if (!jwtProvider.isValid(token)) {
                return
            }

            val userId = jwtProvider.extractUserId(token) ?: return
            val user = runCatching { getUserUseCase.getUser(GetUserQuery(userId)).user }.getOrNull()

            val auth = UsernamePasswordAuthenticationToken(user, null, null)

            val context = SecurityContextHolder.createEmptyContext()
            context.authentication = auth

            SecurityContextHolder.setContext(context)
            securityContextRepository.saveContext(context, request, response)
        } finally {
            filterChain.doFilter(request, response)
        }
    }
}
