package floraguard.reporsitory

import floraguard.model.entity.ZonaEcologica
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ZonaEcologicaRepository extends JpaRepository<ZonaEcologica, Long> {
    List<ZonaEcologica> findByNombreZona(String nombreZona)
}