package floraguard.service

import floraguard.model.entity.Usuario
import floraguard.reporsitory.UsuarioRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UsuarioRepository usuarioRepository

    UserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository
    }

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow { new UsernameNotFoundException("Usuario no encontrado: " + username) }

        def authorities = [
                new SimpleGrantedAuthority("ROLE_${usuario.rolUsuario}")
        ]

        return User.builder()
                .username(usuario.usuario)
                .password(usuario.contrasena) // debe ser BCrypt en DB
                .authorities(authorities)
                .build()
    }
}