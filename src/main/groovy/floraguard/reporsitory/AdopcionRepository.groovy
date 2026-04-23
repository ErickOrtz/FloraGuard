package floraguard.reporsitory

import floraguard.model.entity.Adopcion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AdopcionRepository extends JpaRepository<Adopcion, Long> {

    Optional<Adopcion> findByUsuario_IdAndArbol_Id(Long usuarioId, Long arbolId)

    boolean existsByUsuario_IdAndArbol_Id(Long usuarioId, Long arbolId)

    @Query("SELECT a FROM Adopcion a WHERE a.usuario.id = :usuarioId")
    List<Adopcion> obtenerAdopcionesPorUsuario(@Param("usuarioId") Long usuarioId)

}