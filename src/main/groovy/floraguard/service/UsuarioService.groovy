package floraguard.service

import floraguard.config.SecurityConfig
import floraguard.enums.RolUsuario
import floraguard.model.dto.UsuarioRequest
import floraguard.model.entity.Usuario
import floraguard.reporsitory.UsuarioRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UsuarioService {

    private final UsuarioRepository usuarioRepository
    private final SecurityConfig securityConfig

    UsuarioService(UsuarioRepository usuarioRepository, SecurityConfig securityConfig) {
        this.usuarioRepository = usuarioRepository
        this.securityConfig = securityConfig
    }

    @Transactional(rollbackOn = Exception.class)
    def registrarUsuarioGuardian(UsuarioRequest datosUsuario) {
        try{

            Usuario nuevoUsuario = new Usuario()

            nuevoUsuario.setNombre(datosUsuario.getNombre())
            nuevoUsuario.setApellidoPaterno(datosUsuario.getApellidoPaterno())
            nuevoUsuario.setApellidoMaterno(datosUsuario.getApellidoMaterno())
            nuevoUsuario.setUsuario(datosUsuario.getUsuario())
            nuevoUsuario.setContrasena(securityConfig.passwordEncoder().encode(datosUsuario.getContrasena()))
            nuevoUsuario.setRolUsuario(RolUsuario.USER)
            nuevoUsuario.setCorreo(datosUsuario.getCorreo())
            nuevoUsuario.setTelefono(datosUsuario.getTelefono())

            usuarioRepository.save(nuevoUsuario)

            return "Usuario guardian registrado exitosamente"

        }catch (Exception e){
            throw new RuntimeException("Error al registrar el usuario guardian: " + e.getMessage())
        }
    }

}
