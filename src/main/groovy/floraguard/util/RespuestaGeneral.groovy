package floraguard.util

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

/**
 * Interfaz que proporciona un método para generar respuestas HTTP estructuradas de manera consistente en toda la aplicación.
 * El método `respuestaGeneral` acepta parámetros para indicar el éxito de la operación, un mensaje descriptivo, datos adicionales, un código de error y el estado HTTP.
 * Devuelve una instancia de `ResponseEntity` con un cuerpo que contiene esta información, lo que facilita la creación de respuestas uniformes en los controladores.
 *
 * @Author: Erick Ortiz Gómez
 * @Version: 1.0
 * @Since: 25-04-2026
 */
trait RespuestaGeneral {

    /**
     * Método para generar una respuesta HTTP estructurada de manera consistente.
     * @param success Indica si la operación fue exitosa o no.
     * @param message Mensaje descriptivo sobre el resultado de la operación.
     * @param data Datos adicionales que se desean incluir en la respuesta (opcional).
     * @param error Código de error en caso de que la operación no haya sido exitosa (opcional).
     * @param status Estado HTTP que se desea devolver (por defecto es HttpStatus.OK).
     * @return ResponseEntity con un cuerpo que contiene la información proporcionada.
     * @Author: Erick Ortiz Gómez
     * @Version: 1.0
     * @Since: 25-04-2026
     */
    ResponseEntity<Map> respuestaGeneral(
            boolean success,
            String message,
            data = null,
            int error = null,
            HttpStatus status = HttpStatus.OK
    ) {

        def body = [
                success  : success,
                message  : message,
                data     : data,
                status   : status.value(),
                error    : error,
                timestamp: new Date()
        ]

        return ResponseEntity.status(status).body(body)
    }
}