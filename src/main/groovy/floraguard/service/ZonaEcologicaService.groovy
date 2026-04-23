package floraguard.service

import floraguard.model.dto.ZonaEcologicaDto
import floraguard.model.entity.ZonaEcologica
import floraguard.reporsitory.ZonaEcologicaRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ZonaEcologicaService {

    private final ZonaEcologicaRepository zonaEcologicaRepository

    ZonaEcologicaService(ZonaEcologicaRepository zonaEcologicaRepository) {
        this.zonaEcologicaRepository = zonaEcologicaRepository
    }

    @Transactional(rollbackOn = Exception.class)
    def guardarZonaEcologica(ZonaEcologicaDto datosZonaEcologica) {
        try{
            ZonaEcologica zonaEcologica = new ZonaEcologica()

            zonaEcologica.setNombreZona(datosZonaEcologica.getNombreZona())
            zonaEcologica.setCoordenadas(datosZonaEcologica.getCoordenadas())
            zonaEcologica.setDescripcion(datosZonaEcologica.getDescripcion())

            zonaEcologicaRepository.save(zonaEcologica)
            return "Zona ecológica guardada exitosamente"
        }catch (Exception e) {
            e.printStackTrace()
            throw new Exception("Error al guardar la zona ecológica: ${e.getMessage()}")
        }
    }

}
