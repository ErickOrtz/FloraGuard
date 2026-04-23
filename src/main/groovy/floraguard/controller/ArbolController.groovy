package floraguard.controller

import floraguard.model.dto.UsuarioDto
import floraguard.model.dto.UsuarioRequest
import floraguard.service.AdopcionService
import floraguard.service.ArbolService
import floraguard.util.RespuestaGeneral
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/arbol")
class ArbolController implements RespuestaGeneral {

    private final ArbolService arbolService
    private final AdopcionService adopcionService

    ArbolController(ArbolService arbolService, AdopcionService adopcionService) {
        this.arbolService = arbolService
        this.adopcionService = adopcionService
    }

    @GetMapping("/listar/arboles")
    def listarArboles() {
        try {
            def arboles = arbolService.obtenerArboles()
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

    @GetMapping("obtener/arboles/adoptados")
    def obtenerArbolesAdoptadosPorUsuario(@RequestBody UsuarioRequest usuarioRequest){
        try{
            def arbolesAdoptados = adopcionService.obtenerArbolesAdoptadosPorUsuario(usuarioRequest)
            if (arbolesAdoptados) {
                return respuestaGeneral(true, "Árboles adoptados por el usuario obtenidos exitosamente", arbolesAdoptados,0, HttpStatus.OK)
            } else {
                return respuestaGeneral(false, "No se encontraron árboles adoptados por el usuario con ID: ${idArbol}", null,0, HttpStatus.NOT_FOUND)
            }
        }catch (Exception e){
            e.printStackTrace()
            return respuestaGeneral(false, "Error al obtener los árboles adoptados por el usuario: Error interno del servidor.", null,500, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

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


}
