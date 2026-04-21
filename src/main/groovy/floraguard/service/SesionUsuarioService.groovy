package floraguard.service

import floraguard.model.entity.SesionUsuario
import floraguard.enums.Cliente
import floraguard.reporsitory.SesionUsuarioRepository
import org.springframework.stereotype.Service

import java.security.MessageDigest
import java.sql.Timestamp

@Service
class SesionUsuarioService {

    private final SesionUsuarioRepository sesionUsuarioRepository

    SesionUsuarioService(SesionUsuarioRepository repository) {
        this.sesionUsuarioRepository = repository
    }

    // HASH SHA-256 (guardamos hash del refresh, no el refresh)
    String hashToken(String token) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        byte[] hash = digest.digest(token.bytes)
        return hash.encodeHex().toString()
    }

    private static Timestamp nowTs() {
        return new Timestamp(System.currentTimeMillis())
    }

    // Máximo 3 sesiones activas por usuario (revoca la más vieja)
    void enforceMaxSessions(Long userId, int maxSessions = 3) {

        def sesiones = sesionUsuarioRepository.findByUsuario_IdAndRevokedAtIsNullAndExpiresAtAfter(
                userId,
                nowTs()
        )

        if (sesiones.size() >= maxSessions) {
            def oldest = sesiones.min { it.lastUsedAt ?: it.createdAt }
            oldest.revokedAt = nowTs()
            sesionUsuarioRepository.save(oldest)
        }
    }

    // Crear o actualizar sesión por dispositivo
    void upsertSession(Long userId,
                       String deviceId,
                       Cliente clientType,
                       String refreshToken,
                       Timestamp expiresAt) {

        String hash = hashToken(refreshToken)

        def sesiones = sesionUsuarioRepository.findByUsuario_IdAndRevokedAtIsNullAndExpiresAtAfter(
                userId,
                nowTs()
        )

        def existing = sesiones.find { it.idDispositivo == deviceId }

        if (existing) {
            existing.refreshTokenHash = hash
            existing.lastUsedAt = nowTs()
            existing.expiresAt = expiresAt
            existing.cliente = clientType
            sesionUsuarioRepository.save(existing)
        } else {
            enforceMaxSessions(userId)

            def nueva = new SesionUsuario(
                    idDispositivo: deviceId,
                    cliente: clientType,
                    refreshTokenHash: hash,
                    createdAt: nowTs(),
                    lastUsedAt: nowTs(),
                    expiresAt: expiresAt
            )
            // set relación
            nueva.usuario = new floraguard.model.entity.Usuario(id: userId)

            sesionUsuarioRepository.save(nueva)
        }
    }

    // Buscar sesión válida: userId + deviceId + hash refresh + no revocada + no expirada
    Optional<SesionUsuario> findActiveSession(Long userId,
                                              String deviceId,
                                              String refreshToken) {

        String hash = hashToken(refreshToken)

        def opt = sesionUsuarioRepository.findByUsuario_IdAndIdDispositivoAndRefreshTokenHashAndRevokedAtIsNull(
                userId,
                deviceId,
                hash
        )

        return opt.filter { it.expiresAt != null && it.expiresAt.after(nowTs()) }
    }

    void revokeSession(Long userId, String deviceId) {

        def sesiones = sesionUsuarioRepository.findByUsuario_IdAndRevokedAtIsNullAndExpiresAtAfter(
                userId,
                nowTs()
        )

        sesiones.findAll { it.idDispositivo == deviceId }
                .each {
                    it.revokedAt = nowTs()
                    sesionUsuarioRepository.save(it)
                }
    }
}