package floraguard.controller

import floraguard.service.UsuarioService
import org.springframework.web.bind.annotation.RestController

@RestController("/usuario")
class UsuarioController {

    private final UsuarioService usuarioService

    UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService
    }

}
