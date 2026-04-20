package floraguard.service


import floraguard.entity.model.SesionUsuario
import floraguard.enums.Cliente
import floraguard.reporsitory.SesionUsuarioRepository
import org.springframework.stereotype.Service

import java.security.MessageDigest
import java.time.LocalDateTime

@Service
class SesionUsuarioBizService {
    private final SesionUsuarioRepository sesionUsuarioRepository

    SesionUsuarioBizService(SesionUsuarioRepository repository) {
        this.sesionUsuarioRepository = repository
    }
// 🔥 HASH SHA-256
    String hashToken(String token) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        byte[] hash = digest.digest(token.bytes)
        return hash.encodeHex().toString()
    }

    // 🔥 Máximo 3 sesiones
    void enforceMaxSessions(Integer userId, int maxSessions = 3) {

        def sesiones = sesionUsuarioRepository.findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(
                userId,
                LocalDateTime.now()
        )

        if (sesiones.size() >= maxSessions) {

            def oldest = sesiones.min { it.lastUsedAt ?: it.createdAt }

            oldest.revokedAt = LocalDateTime.now()
            sesionUsuarioRepository.save(oldest)
        }
    }

    // 🔥 Crear o actualizar sesión
    void upsertSession(Integer userId,
                       String deviceId,
                       Cliente clientType,
                       String refreshToken,
                       LocalDateTime expiresAt) {

        String hash = hashToken(refreshToken)

        def sesiones = sesionUsuarioRepository.findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(
                userId,
                LocalDateTime.now()
        )

        def existing = sesiones.find { it.deviceId == deviceId }

        if (existing) {
            existing.refreshTokenHash = hash
            existing.lastUsedAt = LocalDateTime.now()
            existing.expiresAt = expiresAt
            sesionUsuarioRepository.save(existing)
        } else {
            enforceMaxSessions(userId)

            def nueva = new SesionUsuario(
                    userId: userId,
                    deviceId: deviceId,
                    clientType: clientType,
                    refreshTokenHash: hash,
                    createdAt: LocalDateTime.now(),
                    lastUsedAt: LocalDateTime.now(),
                    expiresAt: expiresAt
            )

            sesionUsuarioRepository.save(nueva)
        }
    }

    // 🔍 Buscar sesión válida
    Optional<SesionUsuario> findActiveSession(Integer userId,
                                              String deviceId,
                                              String refreshToken) {

        String hash = hashToken(refreshToken)

        return sesionUsuarioRepository.findByUserIdAndDeviceIdAndRefreshTokenHashAndRevokedAtIsNull(
                userId,
                deviceId,
                hash
        ).filter { it.expiresAt.isAfter(LocalDateTime.now()) }
    }

    void revokeSession(Integer userId, String deviceId) {

        def sesiones = sesionUsuarioRepository.findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(
                userId,
                LocalDateTime.now()
        )

        sesiones.findAll { it.deviceId == deviceId }
                .each {
                    it.revokedAt = LocalDateTime.now()
                    sesionUsuarioRepository.save(it)
                }
    }
}
