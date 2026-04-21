package floraguard.controller

import floraguard.model.dto.EspecieDto
import floraguard.service.EspecieService
import floraguard.util.RespuestaGeneral
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/especie")
class EspecieController implements  RespuestaGeneral
{
    private final EspecieService especieService

    EspecieController(EspecieService especieService) {
        this.especieService = especieService
    }

    @PostMapping("/guardar")
    @Transactional(rollbackOn = Exception.class)
    def guardarEspecie(@RequestBody EspecieDto especieDto)
    {
        println("Recibiendo solicitud para guardar especie: ${especieDto.properties}")
        try{
            def mensaje = especieService.guardarEspecie(especieDto)
            return respuestaGeneral(true,mensaje,null, HttpStatus.OK)
        }catch (Exception e){
            e.printStackTrace()
            return respuestaGeneral(false,"Error al guardar la especie:",null, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/listar")
    def listarEspecies(){
        try{
            def especies = especieService.listarEspecies()
            return respuestaGeneral(true,"Especies listadas exitosamente",especies, HttpStatus.OK)
        }catch (Exception e){
            e.printStackTrace()
            return respuestaGeneral(false,"Error al listar las especies:",null, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
