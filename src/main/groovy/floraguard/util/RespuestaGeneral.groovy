package floraguard.util

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

trait RespuestaGeneral {

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