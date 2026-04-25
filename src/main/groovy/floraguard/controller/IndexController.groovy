package floraguard.controller

import floraguard.util.RespuestaGeneral
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controlador para manejar las solicitudes relacionadas con la página de inicio.
 * Proporciona endpoints para mostrar la página de inicio y manejar cualquier lógica relacionada.
 *
 * Endpoints:
 * - GET /: Muestra la página de inicio.
 *
 * Cada endpoint maneja las excepciones y devuelve una respuesta estructurada utilizando la
 * interfaz RespuestaGeneral, que incluye un mensaje, datos y un código de estado HTTP.
 *
 */
@RestController
class IndexController implements RespuestaGeneral{


    /**
     * Endpoint para mostrar la página de inicio.
     * @return RespuestaGeneral con un mensaje de bienvenida y una descripción de la plataforma o un mensaje de error en caso de que ocurra una excepción.
     */
    @GetMapping("/")
    def index() {
        try {
            def data = [
                    mensaje: "Bienvenido a FloraGuard",
                    descripcion: "FloraGuard es una plataforma dedicada a la conservación y protección de la flora. " +
                            "Aquí puedes adoptar árboles, aprender sobre diferentes especies y contribuir a la preservación del " +
                            "medio ambiente."
            ]
            return respuestaGeneral(false, "Servicio funcionando correctamente.", data, 0, HttpStatus.OK )
        } catch (Exception e) {
            return respuestaGeneral(false, "Error al cargar la página de inicio: ${e.message}", null, 500, HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
