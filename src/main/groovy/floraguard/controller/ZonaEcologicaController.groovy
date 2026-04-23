package floraguard.controller

import floraguard.model.dto.ZonaEcologicaDto
import floraguard.service.ZonaEcologicaService
import floraguard.util.RespuestaGeneral
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/zona-ecologica")
class ZonaEcologicaController implements RespuestaGeneral{

    private final ZonaEcologicaService zonaEcologicaService

    ZonaEcologicaController(ZonaEcologicaService zonaEcologicaService) {
        this.zonaEcologicaService = zonaEcologicaService
    }

    @PostMapping("/guardar")
    @Transactional(rollbackOn = Exception.class)
    def guardarZonaEcologica(@RequestBody ZonaEcologicaDto zonaEcologicaDto) {
        try{
            def mensaje = zonaEcologicaService.guardarZonaEcologica(zonaEcologicaDto)
            return respuestaGeneral(true, mensaje, null, HttpStatus.OK)
        }catch (Exception e){
            e.printStackTrace()
            return respuestaGeneral(false, "Error al guardar la zona ecológica: ${e.getMessage()}", null, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}
