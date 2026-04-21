package floraguard.model.entity

import floraguard.enums.Cliente
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

import java.sql.Timestamp

@Entity
@Table(name = "sesionusuario")
class SesionUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    Usuario usuario

    @Column(name = "idDispositivo")
    String idDispositivo

    @Enumerated(EnumType.STRING)
    @Column(name = "cliente")
    Cliente cliente

    @Column(name = "refreshTokenHash")
    String refreshTokenHash

    @Column(name = "createdAt")
    Timestamp createdAt

    @Column(name = "lastUsedAt")
    Timestamp lastUsedAt

    @Column(name = "expiresAt")
    Timestamp expiresAt

    @Column(name = "revokedAt")
    Timestamp revokedAt
}
