package floraguard.service

import floraguard.model.dto.ArbolDto
import floraguard.model.dto.UsuarioDto
import floraguard.model.dto.UsuarioRequest
import floraguard.model.entity.Adopcion
import floraguard.model.entity.Usuario
import floraguard.reporsitory.AdopcionRepository
import floraguard.reporsitory.ArbolRepository
import floraguard.reporsitory.UsuarioRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime

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

    /**
     * Método para adoptar un árbol por parte de un usuario.
     * Verifica si el árbol ya está adoptado por otro usuario, si el árbol existe y si el usuario existe antes de realizar la adopción.
     * Si la adopción es exitosa, se guarda la información de la adopción en la base de datos.
     *
     * @param idArbol ID del árbol que se desea adoptar.
     * @param usuarioLogueado Usuario que está realizando la adopción.
     * @return Un código que indica el resultado de la operación: 1 para éxito, -1 si el árbol ya está adoptado, -2 si el árbol no existe, -3 si el usuario no existe.
     * @throws Exception Si ocurre un error al realizar la adopción, se lanza una excepción con un mensaje descriptivo.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
     */
    @Transactional(rollbackOn = Exception.class)
    def adoptarArbol(Long idArbol, Usuario usuarioLogueado) {
        try {
            def adopcionActiva = adopcionRepository.obtenerAdopcionPorArbolYActiva(idArbol)
            if (adopcionActiva.isPresent()) return -1

            def arbol = arbolRepository.findById(idArbol)
            if (!arbol.isPresent()) return -2

            def usuarioOpt = usuarioRepository.findByUsuario(usuarioLogueado.getUsuario())

            if (usuarioOpt.isPresent()) {
                def adopcion = new Adopcion()
                adopcion.setUsuario(usuarioOpt.get())
                adopcion.setArbol(arbol.get())
                adopcion.setFechaAdopcion(LocalDate.now() as String)
                adopcion.setActiva(1)
                adopcionRepository.save(adopcion)
                return 1
            } else {
                return -3
            }
        }catch (Exception e) {
            e.printStackTrace()
            throw new Exception("Error al adoptar el árbol: ${e.getMessage()}")
        }
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

    def obtenerArbolesAdoptadosPorUsuario2(String username) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(username)

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
