package floraguard.model.entity

import floraguard.enums.Respuesta
import floraguard.enums.SaludArbol
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

@Entity
@Table(name = "reporteestado")
class ReporteEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idReporte")
    Long id

    @ManyToOne
    @JoinColumn(name = "idArbol")
    Arbol arbol

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    Usuario usuario

    @Column(name = "fechaReporte")
    String fechaReporte

    @Enumerated(EnumType.STRING)
    @Column(name = "salud")
    SaludArbol saludArbol

    @Enumerated(EnumType.STRING)
    @Column(name = "plagas")
    Respuesta plagas

    @Column(name = "observaciones")
    String observaciones
}
