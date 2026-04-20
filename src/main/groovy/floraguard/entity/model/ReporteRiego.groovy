package floraguard.entity.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity(name = "reporteriego")
class ReporteRiego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRiego")
    Long id

    @ManyToOne
    @JoinColumn(name = "idArbol")
    Arbol arbol

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    Usuario usuario

    @Column(name = "fechaRiego")
    String fechaRiego

    @Column(name = "cantidadAgua")
    Double cantidadAgua

    @Column(name = "metodo")
    String metodoRiego

    @Column(name = "Observaciones")
    String observaciones

}
