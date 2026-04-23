package floraguard.service

import floraguard.model.dto.ArbolDto
import floraguard.model.dto.UsuarioRequest
import floraguard.model.entity.Usuario
import floraguard.reporsitory.AdopcionRepository
import floraguard.reporsitory.ArbolRepository
import floraguard.reporsitory.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class AdopcionService {

    private final ArbolRepository arbolRepository
    private final UsuarioRepository usuarioRepository
    private final AdopcionRepository adopcionRepository

    AdopcionService(ArbolRepository arbolRepository, UsuarioRepository usuarioRepository, AdopcionRepository adopcionRepository) {
        this.arbolRepository = arbolRepository
        this.usuarioRepository = usuarioRepository
        this.adopcionRepository = adopcionRepository
    }

    def adoptarArbol(ArbolDto arbolDto, UsuarioRequest usuarioRequest){
        
    }

    def obtenerArbolesAdoptadosPorUsuario(UsuarioRequest usuarioRequest){
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.findByUsuario(usuarioRequest.getUsuario())
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get()
                def adopciones = adopcionRepository.obtenerAdopcionesPorUsuario(usuario.getId())
                def arbolesAdoptados = adopciones.collect { adopcion ->
                    arbolRepository.findById(adopcion.getArbol().getId()).orElse(null)
                }.findAll { it != null }
                return arbolesAdoptados
            } else {
                return []
            }
        }catch (Exception e){
            e.printStackTrace()
            throw new Exception("Error al obtener los árboles adoptados por el usuario: ${e.getMessage()}")
        }
    }
}
