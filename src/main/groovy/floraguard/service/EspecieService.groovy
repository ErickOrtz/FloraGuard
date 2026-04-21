package floraguard.service

import floraguard.model.dto.EspecieDto
import floraguard.model.entity.Especie
import floraguard.reporsitory.EspecieRepository
import org.springframework.stereotype.Service

@Service
class EspecieService {

    private final EspecieRepository especieRepository

    EspecieService(EspecieRepository especieRepository) {
        this.especieRepository = especieRepository
    }

    def guardarEspecie(EspecieDto especieDto){
        try{

            Optional<Especie> especieExistente = especieRepository.findByNombreCientifico(especieDto.getNombreCientifico())
            if(especieExistente.isPresent()){
                return "Error: Ya existe una especie con el mismo nombre científico"
            }

            if(especieDto.getNombreComun() == null || especieDto.getNombreCientifico() == null || especieDto.getTipo() == null || especieDto.getDescripcion() == null){
                return "Error: Todos los campos son obligatorios"
            }

            Especie especie = new Especie()
            especie.setNombreComun(especieDto.getNombreComun())
            especie.setNombreCientifico(especieDto.getNombreCientifico())
            especie.setTipo(especieDto.getTipo())
            especie.setDescripcion(especieDto.getDescripcion())

            especieRepository.save(especie)

            return "Especie guardada exitosamente"
        }catch (Exception e){
            throw new RuntimeException("Error al guardar la especie: ${e.message}")
        }
    }

    def listarEspecies(){
        try{
            return especieRepository.findAll()
        }catch (Exception e){
            throw new RuntimeException("Error al listar las especies: ${e.message}")
        }
    }

    def obtenerEspeciePorId(Long id){
        try{
            Optional<Especie> especieExistente = especieRepository.findById(id)
            if(!especieExistente.isPresent()){
                return "Error: No se encontró la especie con el ID proporcionado"
            }
            return especieExistente.get()
        }catch (Exception e){
            e.printStackTrace()
            return "Error al obtener la especie."
        }
    }

    def actualizarEspecie(Long id, EspecieDto especieDto){
        try{
            Optional<Especie> especieExistente = especieRepository.findById(id)
            if(!especieExistente.isPresent()){
                return "Error: No se encontró la especie con el ID proporcionado"
            }

            Especie especie = especieExistente.get()
            especie.setNombreComun(especieDto.getNombreComun())
            especie.setNombreCientifico(especieDto.getNombreCientifico())
            especie.setTipo(especieDto.getTipo())
            especie.setDescripcion(especieDto.getDescripcion())

            especieRepository.save(especie)

            return "Especie actualizada exitosamente"
        }catch (Exception e){
            e.printStackTrace()
            return "Error al actualizar la especie."
        }
    }

}
