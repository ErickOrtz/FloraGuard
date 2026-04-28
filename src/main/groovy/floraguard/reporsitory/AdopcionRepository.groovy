package floraguard.reporsitory

import floraguard.model.entity.Adopcion
import floraguard.model.entity.Arbol
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AdopcionRepository extends JpaRepository<Adopcion, Long> {

    @Query("SELECT a FROM Adopcion a WHERE a.arbol.id = :idArbol AND a.activa = 1")
    Optional<Adopcion> obtenerAdopcionPorArbolYActiva(@Param("idArbol") Long idArbol)

    @Query("SELECT a FROM Adopcion a WHERE a.usuario.id = :usuarioId")
    List<Adopcion> obtenerAdopcionesPorUsuario(@Param("usuarioId") Long usuarioId)

}