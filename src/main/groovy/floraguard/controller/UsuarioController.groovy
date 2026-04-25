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

    /**
     * Endpoint para obtener la información de un usuario por su correo electrónico.
     * Recibe el correo como parámetro de consulta.
     * Maneja las siguientes situaciones:
     * - Si el usuario es encontrado, devuelve un mensaje de éxito con la información del usuario.
     * - Si el usuario no es encontrado, devuelve un mensaje de error con código NOT_FOUND.
     * - En caso de cualquier excepción, devuelve un mensaje de error genérico con código 500.
     * @Author Erick Ortiz Gómez
     * @Version 1.0
     * @Since 25-04-2026
     * @param correo Correo electrónico del usuario a buscar.
     * @return RespuestaGeneral con el resultado de la operación de búsqueda.
     */
    @GetMapping("/obtener/usuario")
    def obtenerUsuarioPorCorreo(String correo) {
        try {
            def usuario = usuarioService.obtenerUsuarioPorCorreo(correo)
            if (usuario != null) {
                def data = [usuario : usuario]
                return respuestaGeneral(true, "Usuario encontrado",data ,0, HttpStatus.OK)
            } else {
                return respuestaGeneral(false, "Usuario no encontrado", null,400, HttpStatus.NOT_FOUND)
            }
        } catch (Exception e) {
            e.printStackTrace()
            return respuestaGeneral(false, "Error interno del servidor, comunicarse con el administrador.", null, 500, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}
