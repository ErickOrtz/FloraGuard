package floraguard.config.jwt

import io.jsonwebtoken.Claims
import org.springframework.stereotype.Component

import javax.crypto.SecretKey

@Component
class JwtAuthenticationFilter {

    private final String SECRET = "mi_clave_super_secreta_para_jwt_12345678901234567890"

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes())

    private final long ACCESS_EXPIRATION = 1000 * 60 * 15      // 15 min
    private final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7 // 7 días

    String generateAccessToken(String username, List<String> roles) {

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()
    }

    String generateRefreshToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()
    }

    boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
            return true
        } catch (Exception e) {
            return false
        }
    }

    String extractUsername(String token) {
        return getClaims(token).getSubject()
    }

    List<String> extractRoles(String token) {
        return getClaims(token).get("roles", List)
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
    }
}
