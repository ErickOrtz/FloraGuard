package floraguard.model.dto

import floraguard.enums.RolUsuario
import jakarta.persistence.Column
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

class UsuarioResponse {
    String nombre

    String apellidoPaterno

    String apellidoMaterno

    String usuario

    RolUsuario rolUsuario

    String correo

    String telefono

    UsuarioResponse(String nombre, String apellidoPaterno, String apellidoMaterno, String usuario, RolUsuario rolUsuario, String correo, String telefono) {
        this.nombre = nombre
        this.apellidoPaterno = apellidoPaterno
        this.apellidoMaterno = apellidoMaterno
        this.usuario = usuario
        this.rolUsuario = rolUsuario
        this.correo = correo
        this.telefono = telefono
    }

}
