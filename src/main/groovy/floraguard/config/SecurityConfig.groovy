package floraguard.config

import floraguard.config.jwt.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter

    SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf { it.disable() }
                .cors { }
                .sessionManagement {
                    it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                }

                .authorizeHttpRequests {
                    it.requestMatchers("/api/auth/**").permitAll()
                    it.requestMatchers("/api/admin/**").hasRole("ADMIN")
                    it.requestMatchers("/api/**").authenticated()
                    it.anyRequest().permitAll()
                }.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        return http.build()
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        def config = new CorsConfiguration()
        config.setAllowCredentials(true)
        config.setAllowedOrigins([
                "http://localhost:4200", // Angular
                "http://localhost:8100"  // Ionic
        ])
        config.setAllowedHeaders(["*"])
        config.setAllowedMethods(["GET", "POST", "PUT", "DELETE", "OPTIONS"])

        def source = new UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)

        return source
    }

}
