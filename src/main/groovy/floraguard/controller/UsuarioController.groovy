package floraguard.controller

import floraguard.model.dto.UsuarioRequest
import floraguard.model.dto.UsuarioResponse
import floraguard.reporsitory.UsuarioRepository
import floraguard.service.UsuarioService
import floraguard.util.RespuestaGeneral
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
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
 * - GET /api/usuarios/me: Obtiene la información del usuario autenticado.
 *
 * Cada endpoint maneja las excepciones de manera adecuada, devolviendo respuestas con mensajes claros y códigos de estado HTTP correspondientes.
 *
 * @Autor Erick Ortiz Gomez
 * @Version 1.0
 * @Since 2024-06-01
 */
@RestController
@RequestMapping("/usuarios")
class UsuarioController implements RespuestaGeneral {

    private final UsuarioService usuarioService
    private final UsuarioRepository usuarioRepository

    UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService
        this.usuarioRepository = usuarioRepository
    }

    /**
     * Endpoint para obtener la información del usuario autenticado.
     * Verifica si el usuario está autenticado y obtiene su información a partir del contexto de seguridad.
     * Maneja las siguientes situaciones:
     * - Si el usuario no está autenticado, devuelve un mensaje de error con código 401.
     * - Si el usuario autenticado no se encuentra en la base de datos, devuelve un mensaje de error con código 404.
     * - Si ocurre cualquier otra excepción, devuelve un mensaje de error genérico con código 500.
     * - Si el usuario autenticado se encuentra correctamente, devuelve su información con un mensaje de éxito y código 200.
     * @Author Erick Ortiz Gómez
     * @Version 1.0
     * @Since 25-04-2026
     * @return
     */
    @GetMapping("/me")
    def me(){
        try {
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

            def usuarioResponse = new UsuarioResponse(
                    usuarioLogueado.nombre,
                    usuarioLogueado.apellidoPaterno,
                    usuarioLogueado.apellidoMaterno,
                    usuarioLogueado.usuario,
                    usuarioLogueado.rolUsuario,
                    usuarioLogueado.correo,
                    usuarioLogueado.telefono
            )
            return respuestaGeneral(true, "Usuario autenticado", [usuario: usuarioResponse], 0, HttpStatus.OK)
        }catch (Exception e){
            e.printStackTrace()
            return respuestaGeneral(false, "Error interno del servidor, comunicarse con el administrador.", null, 500,HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    /**
     * Endpoint para registrar un nuevo usuario guardián.
     * Recibe un objeto UsuarioRequest con los datos del usuario a registrar.
     * Maneja las siguientes situaciones:
     * - Si el correo ya está registrado, devuelve un mensaje de error con código -1.
     * - Si el nombre de usuario ya está registrado, devuelve un mensaje de error con código -2.
     * - Si el registro es exitoso, devuelve un mensaje de éxito con código 0.
     * - En caso de cualquier excepción, devuelve un mensaje de error genérico con código 500.
     * @Author Erick Ortiz Gómez
     * @Version 1.0
     * @Since 25-04-2026
     * @param datosUsuario Objeto UsuarioRequest con los datos del usuario a registrar.
     * @return RespuestaGeneral con el resultado de la operación de registro.
     */
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

}
