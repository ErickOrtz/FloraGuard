package floraguard.reporsitory

import floraguard.model.entity.SesionUsuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import java.sql.Timestamp

@Repository
interface SesionUsuarioRepository extends JpaRepository<SesionUsuario, Long> {

    List<SesionUsuario> findByUsuario_IdAndRevokedAtIsNullAndExpiresAtAfter(Long idUsuario, Timestamp now)

    Optional<SesionUsuario> findByUsuario_IdAndIdDispositivoAndRefreshTokenHashAndRevokedAtIsNull(
            Long idUsuario,
            String idDispositivo,
            String refreshTokenHash
    )
}