package floraguard.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "zonaecologica")
class ZonaEcologica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idZona")
    Long id

    @Column(name = "nombreZona")
    String nombreZona

    @Column(name = "tipoZona")
    int tipoZona

    @Column(name = "coordenadas")
    String coordenadas

    @Column(name = "descripcion")
    String descripcion
}
