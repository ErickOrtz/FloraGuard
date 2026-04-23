package floraguard.service

import floraguard.config.SecurityConfig
import floraguard.enums.RolUsuario
import floraguard.model.dto.UsuarioRequest
import floraguard.model.entity.Usuario
import floraguard.reporsitory.UsuarioRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

/**
 * Servicio para gestionar las operaciones relacionadas con los usuarios.
 * Proporciona métodos para registrar nuevos usuarios guardianes y obtener usuarios por correo.
 *
 * @author Erick Ortiz Gomez
 * @version 1.0
 * @since 22-04-2026
 */
@Service
class UsuarioService {

    private final UsuarioRepository usuarioRepository
    private final SecurityConfig securityConfig

    UsuarioService(UsuarioRepository usuarioRepository, SecurityConfig securityConfig) {
        this.usuarioRepository = usuarioRepository
        this.securityConfig = securityConfig
    }

    /**
     * Registra un nuevo usuario guardián en el sistema.
     *
     * @param datosUsuario Los datos del usuario a registrar, encapsulados en un objeto UsuarioRequest.
     * @return El usuario registrado o un código de error si el correo o el nombre de usuario ya existen.
     *         -1 si el correo ya está registrado, -2 si el nombre de usuario ya está registrado.
     * @throws RuntimeException Si ocurre un error durante el proceso de registro.
     * @author Erick Ortiz Gomez
     * @version 1.0
     * @since 22-04-2026
     */
    @Transactional(rollbackOn = Exception.class)
    def registrarUsuarioGuardian(UsuarioRequest datosUsuario) {
        try{

            if (usuarioRepository.existByCorreo(datosUsuario.getCorreo())) return -1;
            if (usuarioRepository.existByUsuario(datosUsuario.getUsuario())) return  -2;

            Usuario nuevoUsuario = new Usuario()

            nuevoUsuario.setNombre(datosUsuario.getNombre())
            nuevoUsuario.setApellidoPaterno(datosUsuario.getApellidoPaterno())
            nuevoUsuario.setApellidoMaterno(datosUsuario.getApellidoMaterno())
            nuevoUsuario.setUsuario(datosUsuario.getUsuario())
            nuevoUsuario.setContrasena(securityConfig.passwordEncoder().encode(datosUsuario.getContrasena()))
            nuevoUsuario.setRolUsuario(RolUsuario.USER)
            nuevoUsuario.setCorreo(datosUsuario.getCorreo())
            nuevoUsuario.setTelefono(datosUsuario.getTelefono())

            return usuarioRepository.save(nuevoUsuario)
        }catch (Exception e){
            throw new RuntimeException("Error al registrar el usuario guardian: " + e.getMessage())
        }
    }

    /**
     * Obtiene un usuario por su correo electrónico.
     *
     * @param correo El correo electrónico del usuario a buscar.
     * @return El usuario encontrado o un nuevo objeto Usuario si no se encuentra ningún usuario con el correo proporcionado.
     * @throws RuntimeException Si ocurre un error durante la búsqueda del usuario.
     * @author Erick Ortiz Gomez
     * @version 1.0
     * @since 22-04-2026
     */
    def obtenerUsuarioPorCorreo(String correo) {
        try{
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(correo)
            if (usuarioOpt.isPresent()) {
                return usuarioOpt.get()
            } else return new Usuario()
        }catch (Exception e){
            throw new RuntimeException("Error al obtener el usuario.")
        }
    }

}
