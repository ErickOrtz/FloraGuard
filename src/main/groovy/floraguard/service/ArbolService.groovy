package floraguard.service


import floraguard.model.entity.Arbol
import floraguard.reporsitory.AdopcionRepository
import floraguard.reporsitory.ArbolRepository
import floraguard.reporsitory.UsuarioRepository
import jakarta.transaction.Transactional
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

    def listarArboles() {
        try {
            return arbolRepository.findArbolesDisponibles()
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

    @Transactional(rollbackOn = Exception.class)
    def actualizarNombreArbol(Long id, String nuevoNombre) {
        try {
            def arbol = arbolRepository.findById(id).orElse(null)
            if (arbol) {
                arbol.nombre = nuevoNombre
                arbolRepository.save(arbol)
                return arbol
            } else {
                return null
            }
        } catch (Exception e) {
            e.printStackTrace()
            throw new Exception("Error al actualizar el nombre del árbol: ${e.getMessage()}")
        }
    }
}
