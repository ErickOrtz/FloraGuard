package floraguard.controller

import floraguard.model.dto.UsuarioRequest
import floraguard.service.UsuarioService
import floraguard.util.RespuestaGeneral
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controlador para gestionar las operaciones relacionadas con los usuarios.
 * Proporciona endpoints para registrar usuarios guardianes y obtener información de usuarios por correo.
 *
 * Endpoints:
 * - POST /api/usuarios/registrar/guardian: Registra un nuevo usuario guardián.
 * - GET /api/usuarios/obtener/usuario: Obtiene la información de un usuario por su correo electrónico.
 *
 * Cada endpoint maneja las excepciones de manera adecuada, devolviendo respuestas con mensajes claros y códigos de estado HTTP correspondientes.
 *
 * @Autor Erick Ortiz Gomez
 * @Version 1.0
 * @Since 2024-06-01
 */
@RestController
@RequestMapping("/api/usuarios")
class UsuarioController implements RespuestaGeneral {

    private final UsuarioService usuarioService

    UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService
    }

    @PostMapping("/registrar/guardian")
    def registrarUsuarioGuardian(@RequestBody UsuarioRequest datosUsuario){
        try {
            def guardar = usuarioService.registrarUsuarioGuardian(datosUsuario)
            if (guardar == -1) {
                return respuestaGeneral(false, "El correo ya está registrado", null, -1,HttpStatus.BAD_REQUEST)
            } else if (guardar == -2) {
                return respuestaGeneral(false, "El nombre de usuario ya está registrado", null, -2,HttpStatus.BAD_REQUEST)
            } else {
                return respuestaGeneral(true, "Usuario guardian registrado exitosamente", null, 0, HttpStatus.OK)
            }
        }catch (Exception e){
            e.printStackTrace()
            return respuestaGeneral(false, "Error interno del servidor, comunicarse con el administrador.", null, 500,HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/obtener/usuario")
    def obtenerUsuarioPorCorreo(String correo) {
        try {
            def usuario = usuarioService.obtenerUsuarioPorCorreo(correo)
            if (usuario != null) {
                return respuestaGeneral(true, "Usuario encontrado", usuario, HttpStatus.OK)
            } else {
                return respuestaGeneral(false, "Usuario no encontrado", null, HttpStatus.NOT_FOUND)
            }
        } catch (Exception e) {
            e.printStackTrace()
            return respuestaGeneral(false, "Error interno del servidor, comunicarse con el administrador.", null, 500, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}
