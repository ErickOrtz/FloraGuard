package floraguard.config.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service

import javax.crypto.SecretKey

@Service
class JwtService {

    // TODO: mover a application-local.yml como propiedad (por ahora hardcoded para que puedas probar)
    private static final String SECRET = "mi_clave_super_secreta_para_jwt_12345678901234567890"

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes("UTF-8"))

    private final long ACCESS_EXPIRATION_MS = 1000L * 60 * 15              // 15 min
    private final long REFRESH_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 7    // 7 días

    String generateAccessToken(String username, List<String> roles) {
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()
    }

    String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_MS))
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
        } catch (Exception ignored) {
            return false
        }
    }

    String extractUsername(String token) {
        return claims(token).getSubject()
    }

    List<String> extractRoles(String token) {
        def value = claims(token).get("roles")
        if (value == null) return []
        return (List<String>) value
    }

    Date extractExpiration(String token) {
        return claims(token).getExpiration()
    }

    private Claims claims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
    }
}