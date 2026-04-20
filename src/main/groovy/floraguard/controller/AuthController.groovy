package floraguard.controller

import floraguard.config.jwt.JwtService
import floraguard.reporsitory.UsuarioRepository
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/auth")
class AuthController {


    private final AuthenticationManager authenticationManager
    private final JwtService jwtService
    private final UsuarioRepository usuarioRepository

    AuthController(AuthenticationManager authenticationManager,
                   JwtService jwtService,
                   UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager
        this.jwtService = jwtService
        this.usuarioRepository = usuarioRepository
    }

    @PostMapping("/login")
    def login(@RequestBody Map body, HttpServletResponse response) {

        String username = body.username
        String password = body.password
        String deviceId = body.deviceId
        String clientType = body.clientType // WEB | MOBILE

        // 1. Autenticar
        def auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        )

        UserDetails user = auth.getPrincipal()

        // 2. Obtener roles
        def roles = user.authorities.collect { it.authority }

        // 3. Generar tokens
        String accessToken = jwtService.generateAccessToken(user.username, roles)
        String refreshToken = jwtService.generateRefreshToken(user.username)

        // 👉 Aquí deberías guardar sesión en BD (SesionUsuario)
        // saveSession(user.username, deviceId, clientType, refreshToken)

        // 4. Respuesta según cliente
        if (clientType == "WEB") {

            Cookie cookie = new Cookie("refresh_token", refreshToken)
            cookie.setHttpOnly(true)
            cookie.setPath("/")
            cookie.setMaxAge(60 * 60 * 24 * 7) // 7 días
            // cookie.setSecure(true) // activar en HTTPS

            response.addCookie(cookie)

            return ResponseEntity.ok([
                    accessToken: accessToken
            ])
        }

        // MOBILE
        return ResponseEntity.ok([
                accessToken : accessToken,
                refreshToken: refreshToken
        ])
    }

    // 🔄 REFRESH TOKEN
    @PostMapping("/refresh")
    def refresh(HttpServletRequest request,
                HttpServletResponse response,
                @RequestBody(required = false) Map body) {

        String refreshToken = null

        // WEB → cookie
        if (request.cookies) {
            refreshToken = request.cookies.find { it.name == "refresh_token" }?.value
        }

        // MOBILE → body/header
        if (!refreshToken && body?.refreshToken) {
            refreshToken = body.refreshToken
        }

        if (!refreshToken || !jwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.status(401).body([message: "Refresh token inválido"])
        }

        String username = jwtService.extractUsername(refreshToken)

        def user = usuarioRepository.findByUsuario(username)
        def roles = ["ROLE_" + user.rolUsuario] // ajusta según tu modelo

        // 🔄 rotación
        String newAccess = jwtService.generateAccessToken(username, roles)
        String newRefresh = jwtService.generateRefreshToken(username)

        // 👉 actualizar sesión en BD
        // updateSession(username, newRefresh)

        // WEB → cookie
        if (request.cookies) {

            Cookie cookie = new Cookie("refresh_token", newRefresh)
            cookie.setHttpOnly(true)
            cookie.setPath("/")
            cookie.setMaxAge(60 * 60 * 24 * 7)

            response.addCookie(cookie)

            return ResponseEntity.ok([
                    accessToken: newAccess
            ])
        }

        // MOBILE
        return ResponseEntity.ok([
                accessToken : newAccess,
                refreshToken: newRefresh
        ])
    }

    // 🚪 LOGOUT
    @PostMapping("/logout")
    def logout(HttpServletRequest request,
               HttpServletResponse response,
               @RequestBody(required = false) Map body) {

        String deviceId = body?.deviceId

        // 👉 revocar sesión en BD
        // revokeSession(deviceId)

        // borrar cookie si es WEB
        if (request.cookies) {
            Cookie cookie = new Cookie("refresh_token", null)
            cookie.setHttpOnly(true)
            cookie.setPath("/")
            cookie.setMaxAge(0)
            response.addCookie(cookie)
        }

        return ResponseEntity.ok([
                message: "Logout exitoso"
        ])
    }
}
