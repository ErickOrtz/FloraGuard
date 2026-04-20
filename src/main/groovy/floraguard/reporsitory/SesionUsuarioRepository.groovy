package floraguard.reporsitory

import floraguard.entity.model.SesionUsuario
import org.springframework.stereotype.Repository

import java.time.LocalDateTime

@Repository
interface SesionUsuarioRepository {

    List<SesionUsuario> findByUserIdAndRevokedAtIsNullAndExpiresAtAfter(Integer userId, LocalDateTime now)

    Optional<SesionUsuario> findByUserIdAndDeviceIdAndRefreshTokenHashAndRevokedAtIsNull(
            Integer userId,
            String deviceId,
            String refreshTokenHash
    )
}