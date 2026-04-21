package floraguard.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "adopcion")
class Adopcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAdopcion")
    Long id

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    Usuario usuario

    @ManyToOne
    @JoinColumn(name = "idArbol")
    Arbol arbol

    @Column(name = "fechaAdopcion")
    String fechaAdopcion
}
