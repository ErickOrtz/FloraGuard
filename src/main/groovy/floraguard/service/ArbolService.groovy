package floraguard.service

import floraguard.model.dto.UsuarioRequest
import floraguard.model.entity.Arbol
import floraguard.model.entity.Usuario
import floraguard.reporsitory.AdopcionRepository
import floraguard.reporsitory.ArbolRepository
import floraguard.reporsitory.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class ArbolService {

    private final ArbolRepository arbolRepository
    private final UsuarioRepository usuarioRepository
    private final AdopcionRepository adopcionRepository

    ArbolService(ArbolRepository arbolRepository, UsuarioRepository usuarioRepository, AdopcionRepository adopcionRepository) {
        this.arbolRepository = arbolRepository
        this.usuarioRepository = usuarioRepository
        this.adopcionRepository = adopcionRepository
    }

    def obtenerArboles() {
        try {
            return arbolRepository.findAll()
        } catch (Exception e) {
            e.printStackTrace()
            throw new Exception("Error al obtener los árboles: ${e.getMessage()}")
        }
    }

    def obtenerArbolPorId(Long id) {
        try {
            return arbolRepository.findById(id).orElse(new Arbol())
        } catch (Exception e) {
            e.printStackTrace()
            throw new Exception("Error al obtener el árbol por ID: ${e.getMessage()}")
        }
    }
}
