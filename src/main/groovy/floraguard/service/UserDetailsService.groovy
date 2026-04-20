package floraguard.service

import floraguard.entity.model.Usuario
import floraguard.reporsitory.UsuarioRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsService {
    private final UsuarioRepository usuarioRepository

    UserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository
    }

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow {
                    new UsernameNotFoundException("Usuario no encontrado")
                }

        // 🔥 IMPORTANTE: Spring espera ROLE_
        def authorities = [
                new SimpleGrantedAuthority("ROLE_${usuario.rolUsuario}")
        ]

        return new Usuario(
                usuario.usuario,          // username
                usuario.contrasena,      // password (ya encriptado)
                authorities
        )
    }
}
