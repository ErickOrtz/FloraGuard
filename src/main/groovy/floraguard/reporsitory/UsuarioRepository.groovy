package floraguard.reporsitory

import floraguard.entity.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> finById(Long id)

     Optional<Usuario> findByEmail(String email)
}