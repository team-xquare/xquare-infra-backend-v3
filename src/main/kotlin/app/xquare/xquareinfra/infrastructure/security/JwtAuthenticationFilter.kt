package app.xquare.xquareinfra.infrastructure.security

import app.xquare.xquareinfra.application.user.ports.inbound.GetUserQuery
import app.xquare.xquareinfra.application.user.ports.inbound.GetUserResult
import app.xquare.xquareinfra.application.user.ports.inbound.GetUserUseCase
import app.xquare.xquareinfra.infrastructure.jwt.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val getUserUseCase: GetUserUseCase,
    private val jwtProvider: JwtProvider,
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

            val user =
                when (val userResult = getUserUseCase.getUser(GetUserQuery(userId))) {
                    is GetUserResult.Success -> userResult.user
                    else -> return
                }

            val auth = UsernamePasswordAuthenticationToken(user, null, null)
            SecurityContextHolder.getContext().authentication = auth
        } finally {
            filterChain.doFilter(request, response)
        }
    }
}
