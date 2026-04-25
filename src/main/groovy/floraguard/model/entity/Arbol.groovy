package floraguard.model.entity

import floraguard.enums.EstadoArbol
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
@Table(name = "arbol")
class Arbol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idArbol")
    Long id

    @ManyToOne
    @JoinColumn(name = "idEspecie")
    Especie idEspecie

    @ManyToOne
    @JoinColumn(name = "idZona")
    ZonaEcologica idZona

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    EstadoArbol estado

    @Column(name = "coordenadas")
    String coordenadas

    @Column(name = "fechaPlantado")
    String fechaPlantado

    @Column(name = "descripcion")
    String descripcion

    @Column(name = "urlImagen")
    String urlImagen

    @Column(name = "nombre")
    String nombre
}
