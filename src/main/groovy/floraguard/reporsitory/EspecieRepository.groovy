package floraguard.reporsitory

import floraguard.entity.model.Especie
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EspecieRepository extends JpaRepository<Especie, Long> {

    Optional<Especie> findByNombreCientifico(String nombreCientifico)
}