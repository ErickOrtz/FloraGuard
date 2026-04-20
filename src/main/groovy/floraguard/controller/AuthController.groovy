package floraguard.controller

import floraguard.config.jwt.JwtService
import floraguard.enums.Cliente
import floraguard.reporsitory.UsuarioRepository
import floraguard.service.SesionUsuarioService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.Timestamp

import java.time.LocalDateTime
import java.time.ZoneId

@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final AuthenticationManager authenticationManager
    private final JwtService jwtService
    private final UsuarioRepository usuarioRepository
    private final SesionUsuarioService sesionUsuarioService

    AuthController(AuthenticationManager authenticationManager,
                   JwtService jwtService,
                   UsuarioRepository usuarioRepository,
                   SesionUsuarioService sesionUsuarioService) {
        this.authenticationManager = authenticationManager
        this.jwtService = jwtService
        this.usuarioRepository = usuarioRepository
        this.sesionUsuarioService = sesionUsuarioService
    }

    @PostMapping("/login")
    def login(@RequestBody Map body, HttpServletResponse response) {

        String username = (String) body.username
        String password = (String) body.password
        String deviceId = (String) body.deviceId
        String clientTypeRaw = (String) body.clientType // WEB | MOBILE

        if (!username || !password || !deviceId || !clientTypeRaw) {
            return ResponseEntity.badRequest().body([message: "username, password, deviceId y clientType son requeridos"])
        }

        Cliente clientType = Cliente.valueOf(clientTypeRaw)

        def auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        )

        UserDetails user = (UserDetails) auth.getPrincipal()
        def roles = user.authorities.collect { it.authority }

        String accessToken = jwtService.generateAccessToken(user.username, roles)
        String refreshToken = jwtService.generateRefreshToken(user.username)

        // buscar usuario id (para sesiones)
        def usuarioOpt = usuarioRepository.findByUsuario(user.username)
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body([message: "Usuario no existe"])
        }
        def usuario = usuarioOpt.get()

        // expiresAt según token refresh
        Timestamp expiresAt = new Timestamp(jwtService.extractExpiration(refreshToken).time)

        // guarda/rota sesión + aplica máximo 3
        sesionUsuarioService.upsertSession(usuario.id as Long, deviceId, clientType, refreshToken, expiresAt)

        if (clientType == Cliente.WEB) {
            Cookie cookie = new Cookie("refresh_token", refreshToken)
            cookie.setHttpOnly(true)
            cookie.setPath("/")
            cookie.setMaxAge(60 * 60 * 24 * 7)
            // cookie.setSecure(true) // PROD con HTTPS
            response.addCookie(cookie)

            return ResponseEntity.ok([accessToken: accessToken])
        }

        return ResponseEntity.ok([accessToken: accessToken, refreshToken: refreshToken])
    }

    @PostMapping("/refresh")
    def refresh(HttpServletRequest request,
                HttpServletResponse response,
                @RequestBody(required = false) Map body) {

        String deviceId = body?.deviceId as String
        String clientTypeRaw = body?.clientType as String

        // WEB podría no mandar body, pero IGUAL necesitamos deviceId para mapear sesión
        if (!deviceId) {
            // permitir deviceId por header también
            deviceId = request.getHeader("X-Device-Id")
        }

        String refreshToken = null

        // WEB → cookie
        if (request.cookies) {
            refreshToken = request.cookies.find { it.name == "refresh_token" }?.value
        }

        // MOBILE → body
        if (!refreshToken && body?.refreshToken) {
            refreshToken = body.refreshToken as String
        }

        if (!refreshToken || !jwtService.isTokenValid(refreshToken)) {
            return ResponseEntity.status(401).body([message: "Refresh token inválido"])
        }

        String username = jwtService.extractUsername(refreshToken)

        def usuarioOpt = usuarioRepository.findByUsuario(username)
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body([message: "Usuario no existe"])
        }
        def usuario = usuarioOpt.get()

        if (!deviceId) {
            return ResponseEntity.badRequest().body([message: "deviceId es requerido para refresh"])
        }

        // valida sesión en BD (hash + deviceId + no revocada + no expirada)
        def sesionOpt = sesionUsuarioService.findActiveSession(usuario.id as Long, deviceId, refreshToken)
        if (sesionOpt.isEmpty()) {
            return ResponseEntity.status(401).body([message: "Sesión no válida (revocada/expirada/token rotado)"])
        }

        def roles = ["ROLE_${usuario.rolUsuario}"]

        String newAccess = jwtService.generateAccessToken(username, roles)
        String newRefresh = jwtService.generateRefreshToken(username)

        Timestamp newExpiresAt = new Timestamp(jwtService.extractExpiration(newRefresh).time)

        // rota sesión guardando nuevo hash
        Cliente clientType = clientTypeRaw ? Cliente.valueOf(clientTypeRaw) : (sesionOpt.get().cliente as Cliente)
        sesionUsuarioService.upsertSession(usuario.id as Long, deviceId, clientType, newRefresh, newExpiresAt)

        // Si venía por cookie, asumimos WEB y devolvemos cookie nueva
        if (request.cookies) {
            Cookie cookie = new Cookie("refresh_token", newRefresh)
            cookie.setHttpOnly(true)
            cookie.setPath("/")
            cookie.setMaxAge(60 * 60 * 24 * 7)
            response.addCookie(cookie)

            return ResponseEntity.ok([accessToken: newAccess])
        }

        // MOBILE
        return ResponseEntity.ok([accessToken: newAccess, refreshToken: newRefresh])
    }

    @PostMapping("/logout")
    def logout(HttpServletRequest request,
               HttpServletResponse response,
               @RequestBody(required = false) Map body) {

        String deviceId = body?.deviceId as String
        if (!deviceId) deviceId = request.getHeader("X-Device-Id")

        // si no hay deviceId, al menos borrar cookie (web)
        if (deviceId) {
            // identificar usuario por refresh cookie si existe (opcional, aquí lo dejamos simple: revoca por deviceId si puedes)
            // En una versión mejor: extraer username del refresh token y revocar (userId, deviceId).
        }

        // borrar cookie si es WEB
        if (request.cookies) {
            Cookie cookie = new Cookie("refresh_token", null)
            cookie.setHttpOnly(true)
            cookie.setPath("/")
            cookie.setMaxAge(0)
            response.addCookie(cookie)
        }

        return ResponseEntity.ok([message: "Logout exitoso"])
    }
}