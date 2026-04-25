package floraguard.reporsitory

import floraguard.model.entity.Arbol
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Repositorio para la entidad Arbol, que extiende JpaRepository para proporcionar operaciones CRUD básicas.
 *
 * @Author: Erick Ortiz Gómez
 * @Version: 1.0
 * @Since: 25-04-2026
 */
@Repository
interface ArbolRepository extends JpaRepository<Arbol, Long> {

    /**
     * Consulta personalizada para obtener los árboles que no están adoptados actualmente.
     * @return Lista de árboles disponibles para adopción.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
     */
    @Query(value = '''SELECT a.* 
                FROM arbol a 
                LEFT JOIN adopcion ad 
                ON ad.idArbol = a.idArbol 
                AND ad.activa = 1 
                WHERE ad.idAdopcion IS NULL''', nativeQuery = true)
    List<Arbol> findArbolesDisponibles()
}