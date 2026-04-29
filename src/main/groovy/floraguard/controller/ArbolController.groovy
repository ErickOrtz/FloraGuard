package floraguard.controller


import floraguard.model.dto.UsuarioRequest
import floraguard.service.AdopcionService
import floraguard.service.ArbolService
import floraguard.util.RespuestaGeneral
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * Controlador para manejar las operaciones relacionadas con los árboles.
 * Proporciona endpoints para listar árboles, obtener árboles adoptados por un usuario y obtener un árbol por su ID.
 *
 * Endpoints:
 * - GET /arbol/listar/arboles: Lista todos los árboles disponibles.
 * - GET /arbol/obtener/arboles/adoptados: Obtiene los árboles adoptados por un usuario específico.
 * - GET /arbol/obtener/arbol: Obtiene un árbol por su ID.
 *
 * Cada endpoint maneja las excepciones y devuelve una respuesta estructurada utilizando la
 * interfaz RespuestaGeneral, que incluye un mensaje, datos y un código de estado HTTP.
 *
 * @Author: Erick Ortiz Gómez
 * @Version: 1.0
 * @Since: 25-04-2026
 */
@RestController
@RequestMapping("/arbol")
class ArbolController implements RespuestaGeneral {

    private final ArbolService arbolService
    private final AdopcionService adopcionService

    ArbolController(ArbolService arbolService, AdopcionService adopcionService) {
        this.arbolService = arbolService
        this.adopcionService = adopcionService
    }

    /**
     * Endpoint para listar todos los árboles disponibles.
     * @return RespuestaGeneral con la lista de árboles o un mensaje de error.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
     */
    @GetMapping("/listar/arboles")
    def listarArboles() {
        try {
            def arboles = arbolService.listarArboles()
            if (arboles) {
                return respuestaGeneral(true, "Árboles listados exitosamente", arboles,0, HttpStatus.OK)
            } else {
                return respuestaGeneral(false, "No se encontraron árboles", null,0, HttpStatus.NOT_FOUND)
            }
        } catch (Exception e) {
            e.printStackTrace()
            return respuestaGeneral(false, "Error al listar los árboles: Error interno del servidor.", null,500, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Endpoint para obtener los árboles adoptados por un usuario específico.
     * @param usuarioRequest Objeto que contiene la información del usuario para el cual se desean obtener los árboles adoptados.
     * @return RespuestaGeneral con la lista de árboles adoptados por el usuario o un mensaje de error.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
     */
    @GetMapping("obtener/arboles/adoptados")
    def obtenerArbolesAdoptadosPorUsuario(){
        try{

            def auth = SecurityContextHolder.getContext().getAuthentication()

            if (auth == null || !auth.isAuthenticated() || auth.principal == "anonymousUser") {
                return respuestaGeneral(false, "Usuario no autenticado", null, 401, HttpStatus.UNAUTHORIZED)
            }

            String username = auth.name

            def arbolesAdoptados = adopcionService.obtenerArbolesAdoptadosPorUsuario(username)

            if (arbolesAdoptados) {
                return respuestaGeneral(true, "Árboles adoptados por el usuario obtenidos exitosamente", arbolesAdoptados,0, HttpStatus.OK)
            } else {
                return respuestaGeneral(false, "No se encontraron árboles adoptados por el usuario: ${usuarioRequest.getUsuario()}", null,0, HttpStatus.NOT_FOUND)
            }
        }catch (Exception e){
            e.printStackTrace()
            return respuestaGeneral(false, "Error al obtener los árboles adoptados por el usuario: Error interno del servidor.", null,500, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Endpoint para obtener un árbol por su ID.
     * @param idArbol ID del árbol que se desea obtener.
     * @return RespuestaGeneral con el árbol obtenido o un mensaje de error.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
    */
    @GetMapping("obtener/arbol")
    def obtenerArbolPorId(@RequestParam("idArbol") Long idArbol) {
        try {
            def arbol = arbolService.obtenerArbolPorId(idArbol)
            if (arbol) {
                return respuestaGeneral(true, "Árbol obtenido exitosamente", arbol,0, HttpStatus.OK)
            } else {
                return respuestaGeneral(false, "No se encontró el árbol con ID: ${idArbol}", null,0, HttpStatus.NOT_FOUND)
            }
        } catch (Exception e) {
            e.printStackTrace()
            return respuestaGeneral(false, "Error al obtener el árbol por ID: Error interno del servidor.", null,500, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Endpoint para cambiar el nombre de un árbol específico.
     * @param idArbol ID del árbol cuyo nombre se desea cambiar.
     * @param nuevoNombre Nuevo nombre que se asignará al árbol.
     * @return RespuestaGeneral con el árbol actualizado o un mensaje de error.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
    */
    @PostMapping("/cambiar/nombre/arbol")
    def cambiarNombreArbol(@RequestParam("idArbol") Long idArbol,
                           @RequestParam("nombreNuevo") String nuevoNombre) {
        try {
            def arbol = arbolService.actualizarNombreArbol(idArbol, nuevoNombre)
            if (arbol) {
                return respuestaGeneral(true, "Nombre del árbol actualizado exitosamente", arbol,0, HttpStatus.OK)
            } else {
                return respuestaGeneral(false, "No se encontró el árbol con ID: ${idArbol}", null,0, HttpStatus.NOT_FOUND)
            }
        }catch (Exception e) {
            e.printStackTrace()
            return respuestaGeneral(false, "Error al cambiar el nombre del árbol: Error interno del servidor.", null,500, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}
