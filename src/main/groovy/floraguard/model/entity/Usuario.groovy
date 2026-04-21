package floraguard.model.entity

import floraguard.enums.RolUsuario
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "usuario")
class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    Long id

    @Column(name = "nombre")
    String nombre

    @Column(name = "apellidoPaterno")
    String apellidoPaterno

    @Column(name = "apellidoMaterno")
    String apellidoMaterno

    @Column(name = "usuario")
    String usuario

    @Column(name = "contrasenia")
    String contrasena

    @Enumerated(EnumType.STRING)
    @Column(name = "rolUsuario")
    RolUsuario rolUsuario

    @Column(name = "correo")
    String correo

    @Column(name = "telefono")
    String telefono
}
