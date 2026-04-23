package floraguard.reporsitory

import floraguard.model.entity.Arbol
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArbolRepository extends JpaRepository<Arbol, Long> {
}