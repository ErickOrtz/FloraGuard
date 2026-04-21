package floraguard.controller

import floraguard.model.dto.UsuarioRequest
import floraguard.service.UsuarioService
import floraguard.util.RespuestaGeneral
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UsuarioController implements RespuestaGeneral{

    private final UsuarioService usuarioService

    UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService
    }

    @PostMapping("/registrar/guardian")
    def registrarUsuarioGuardian(@RequestBody UsuarioRequest datosUsuario){
        try {
            def mensaje = usuarioService.registrarUsuarioGuardian(datosUsuario)
            return respuestaGeneral(true, mensaje, null, HttpStatus.OK)
        }catch (Exception e){
            return respuestaGeneral(false, e.getMessage(), null, HttpStatus.BAD_REQUEST)
        }
    }
}
