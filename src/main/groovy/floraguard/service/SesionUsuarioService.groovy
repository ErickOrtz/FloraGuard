package floraguard.service

import floraguard.model.entity.SesionUsuario
import floraguard.enums.Cliente
import floraguard.reporsitory.SesionUsuarioRepository
import org.springframework.stereotype.Service

import java.security.MessageDigest
import java.sql.Timestamp

/**
 * Servicio para manejar las sesiones de usuario, incluyendo la creación, actualización, búsqueda y revocación de sesiones.
 * Utiliza un hash SHA-256 para almacenar el refresh token de forma segura.
 * Limita el número de sesiones activas por usuario a un máximo configurable (por defecto 3).
 * Proporciona métodos para:
 * - EnforceMaxSessions: Revocar la sesión más antigua si se excede el límite de sesiones activas.
 * - UpsertSession: Crear o actualizar una sesión por dispositivo, revocando si es necesario.
 * - FindActiveSession: Buscar una sesión válida por userId, deviceId y hash del refresh token.
 * - RevokeSession: Revocar sesiones por userId y deviceId.
 *
 * @Author: Erick Ortiz Gómez
 * @Version: 1.0
 * @Since: 25-04-2026
 */
@Service
class SesionUsuarioService {

    private final SesionUsuarioRepository sesionUsuarioRepository

    SesionUsuarioService(SesionUsuarioRepository repository) {
        this.sesionUsuarioRepository = repository
    }

    /**
     * Genera un hash SHA-256 a partir del token proporcionado.
     * Este método se utiliza para almacenar de forma segura el refresh token en la base de datos.
     *
     * @param token El refresh token que se desea hashear.
     * @return Un string que representa el hash SHA-256 del token.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
     */
    String hashToken(String token) {
        MessageDigest digest = MessageDigest.getInstance("SHA-256")
        byte[] hash = digest.digest(token.bytes)
        return hash.encodeHex().toString()
    }

    /**
     * Obtiene la marca de tiempo actual.
     *
     * @return Un objeto Timestamp que representa el momento actual.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
     */
    private static Timestamp nowTs() {
        return new Timestamp(System.currentTimeMillis())
    }

    /**
     * EnforceMaxSessions se encarga de garantizar que un usuario no tenga más de un número máximo de sesiones activas (por defecto 3).
     * Si el número de sesiones activas para el usuario excede el límite, se revoca la sesión más antigua.
     *
     * @param userId El ID del usuario para el cual se desea aplicar la restricción de sesiones.
     * @param maxSessions El número máximo de sesiones activas permitidas para el usuario (por defecto 3).
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
     */
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

    /**
     * UpsertSession se encarga de crear o actualizar una sesión de usuario para un dispositivo específico. Si ya existe una sesión para el mismo usuario y
     * dispositivo (incluso si está revocada), se actualiza con el nuevo refresh token, fecha de expiración y tipo de cliente.
     * Si no existe una sesión para ese usuario y dispositivo, se crea una nueva sesión.
     * Antes de crear una nueva sesión, se llama a enforceMaxSessions para garantizar que el
     * usuario no exceda el número máximo de sesiones activas permitidas.
     * @param userId El ID del usuario para el cual se desea crear o actualizar la sesión.
     * @param deviceId El ID del dispositivo asociado a la sesión.
     * @param clientType El tipo de cliente (por ejemplo, WEB o MOBILE) asociado a la sesión.
     * @param refreshToken El refresh token que se desea almacenar para la sesión.
     * @param expiresAt La fecha de expiración del refresh token.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
     */
    void upsertSession(Long userId,
                       String deviceId,
                       Cliente clientType,
                       String refreshToken,
                       Timestamp expiresAt) {

        String hash = hashToken(refreshToken)

        // Buscar sesión existente por usuario y deviceId (incluyendo revocadas)
        def existenteOpt = sesionUsuarioRepository.findAll().find {
            it.usuario?.id == userId && it.idDispositivo == deviceId
        }

        if (existenteOpt) {
            def existente = existenteOpt
            existente.refreshTokenHash = hash
            existente.lastUsedAt = nowTs()
            existente.expiresAt = expiresAt
            existente.cliente = clientType
            existente.revokedAt = null // Reactiva si estaba revocada
            sesionUsuarioRepository.save(existente)
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

    /**
     * Busca una sesión de usuario activa y válida basada en el ID del usuario, el ID del dispositivo y el refresh token proporcionado.
     * La sesión se considera válida si no ha sido revocada, no ha expirado y el hash del refresh token coincide con el almacenado en la base de datos.
     *
     * @param userId El ID del usuario para el cual se desea buscar la sesión.
     * @param deviceId El ID del dispositivo asociado a la sesión que se desea buscar.
     * @param refreshToken El refresh token que se desea validar para encontrar la sesión correspondiente.
     * @return Un Optional que contiene la SesionUsuario encontrada si es válida, o vacío si no se encuentra ninguna sesión válida.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
     */
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

    /**
     * RevokeSession se encarga de revocar todas las sesiones activas de un usuario para un dispositivo específico. Esto se logra estableciendo la marca de tiempo de revocación (revokedAt) en el momento actual para todas las sesiones que coincidan con el userId, deviceId y que no hayan sido revocadas previamente.
     *
     * @param userId El ID del usuario para el cual se desea revocar las sesiones.
     * @param deviceId El ID del dispositivo asociado a las sesiones que se desean revocar.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
     */
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