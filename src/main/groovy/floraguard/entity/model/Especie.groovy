package floraguard.entity.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity(name = "especie")
class Especie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idEspecie")
    Long id

    @Column(name = "nombreComun")
    String nombreComun

    @Column(name = "nombreCientifico")
    String nombreCientifico

    @Column(name = "tipo")
    String tipo

    @Column(name = "descripcion")
    String descripcion

}