package floraguard.service

import floraguard.model.dto.ArbolDto
import floraguard.model.dto.UsuarioRequest
import floraguard.model.entity.Usuario
import floraguard.reporsitory.AdopcionRepository
import floraguard.reporsitory.ArbolRepository
import floraguard.reporsitory.UsuarioRepository
import org.springframework.stereotype.Service

/**
 * Servicio para gestionar las adopciones de árboles por parte de los usuarios.
 * Proporciona métodos para adoptar un árbol y obtener los árboles adoptados por un usuario específico.
 *
 * @Author: Erick Ortiz Gómez
 * @Version: 1.0
 * @Since: 25-04-2026
 */
@Service
class AdopcionService {

    private final ArbolRepository arbolRepository
    private final UsuarioRepository usuarioRepository
    private final AdopcionRepository adopcionRepository

    AdopcionService(ArbolRepository arbolRepository, UsuarioRepository usuarioRepository, AdopcionRepository adopcionRepository) {
        this.arbolRepository = arbolRepository
        this.usuarioRepository = usuarioRepository
        this.adopcionRepository = adopcionRepository
    }

    def adoptarArbol(ArbolDto arbolDto, UsuarioRequest usuarioRequest) {

    }

    /**
     * Método para obtener los árboles adoptados por un usuario específico.
     * Busca al usuario en la base de datos utilizando su nombre de usuario y luego obtiene las adopciones asociadas a ese usuario.
     * A partir de las adopciones, se obtienen los árboles adoptados y se devuelve la lista de árboles.
     *
     * @param usuarioRequest Objeto que contiene la información del usuario para el cual se desean obtener los árboles adoptados.
     * @return Lista de árboles adoptados por el usuario o una lista vacía si el usuario no existe o no tiene adopciones.
     * @throws Exception Si ocurre un error al obtener los árboles adoptados, se lanza una excepción con un mensaje descriptivo.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
     */
    def obtenerArbolesAdoptadosPorUsuario(UsuarioRequest usuarioRequest) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(usuarioRequest.getUsuario())
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get()
                def adopciones = adopcionRepository.obtenerAdopcionesPorUsuario(usuario.getId())
                def arbolesAdoptados = adopciones.collect { adopcion ->
                    arbolRepository.findById(adopcion.getArbol().getId()).orElse(null)
                }.findAll { it != null }
                return arbolesAdoptados
            } else {
                return []
            }
        } catch (Exception e) {
            e.printStackTrace()
            throw new Exception("Error al obtener los árboles adoptados por el usuario: ${e.getMessage()}")
        }
    }
}
