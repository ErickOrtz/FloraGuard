package floraguard.config.jwt

import floraguard.service.UserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService
    private final UserDetailsService userDetailsService

    JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService
        this.userDetailsService = userDetailsService
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization")
        if (!authHeader || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        String token = authHeader.substring(7)
        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response)
            return
        }

        String username = jwtService.extractUsername(token)

        if (username && SecurityContextHolder.getContext().getAuthentication() == null) {
            def userDetails = userDetailsService.loadUserByUsername(username)

            def authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
            )
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))
            SecurityContextHolder.getContext().setAuthentication(authToken)
        }

        filterChain.doFilter(request, response)
    }
}