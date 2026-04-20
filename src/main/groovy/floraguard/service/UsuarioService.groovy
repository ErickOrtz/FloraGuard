package floraguard.service

import floraguard.reporsitory.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class UsuarioService {

    private final UsuarioRepository usuarioRepository

    UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository
    }

}
