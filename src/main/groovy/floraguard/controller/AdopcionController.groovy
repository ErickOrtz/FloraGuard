package floraguard.controller

import floraguard.reporsitory.UsuarioRepository
import floraguard.service.AdopcionService
import floraguard.util.RespuestaGeneral
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/adopciones")
class AdopcionController implements RespuestaGeneral{

    private final AdopcionService adopcionService
    private final UsuarioRepository usuarioRepository

    AdopcionController(AdopcionService adopcionService, UsuarioRepository usuarioRepository) {
        this.adopcionService = adopcionService
        this.usuarioRepository = usuarioRepository
    }

    @PostMapping("/adoptar")
    def adoptarArbol(@RequestParam("idArbol") Long idArbol) {
        try{
            def auth = SecurityContextHolder.getContext().getAuthentication()
            if (auth == null || !auth.isAuthenticated() || auth.principal == null || auth.principal == "anonymousUser") {
                return respuestaGeneral(false, "Usuario no autenticado", null, 401, HttpStatus.UNAUTHORIZED)
            }

            String username
            if (auth.principal instanceof UserDetails) {
                username = ((UserDetails) auth.principal).username
            } else {
                // fallback si el principal viene como String
                username = auth.name
            }

            def usuarioOpt = usuarioRepository.findByUsuario(username)
            if (usuarioOpt.isEmpty()) {
                return  respuestaGeneral(false, "Usuario no encontrado", null, 404, HttpStatus.NOT_FOUND)
            }

            def usuarioLogueado = usuarioOpt.get()

            def resultado = adopcionService.adoptarArbol(idArbol,usuarioLogueado)

            switch (resultado) {
                case 1:
                    return respuestaGeneral(true, "Árbol adoptado exitosamente", null, 0, HttpStatus.OK)
                case -1:
                    return respuestaGeneral(false, "El árbol ya está adoptado por otro usuario", null, -1, HttpStatus.CONFLICT)
                case -2:
                    return respuestaGeneral(false, "Árbol no encontrado", null, -2, HttpStatus.NOT_FOUND)
                case -3:
                    return respuestaGeneral(false, "Usuario no encontrado", null, -3, HttpStatus.NOT_FOUND)
                default:
                    return respuestaGeneral(false, "Error desconocido al adoptar el árbol", null, 500, HttpStatus.INTERNAL_SERVER_ERROR)
            }

        }catch (Exception e) {
            e.printStackTrace()
            return "Error al adoptar el árbol: ${e.getMessage()}"
        }
    }
}
