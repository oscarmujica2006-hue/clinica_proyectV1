package proyect_final.clinica.Model.Entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "detalle_revision")
public class DetalleRevision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalleRev;

    @ManyToOne
    @JoinColumn(name = "id_revision", nullable = false)
    private Revision revision;

    @ManyToOne
    @JoinColumn(name = "id_diente", nullable = false)
    private Diente diente;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "observaciones")
    private String observaciones;

    @OneToMany(mappedBy = "detalleRevision", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CaraDetRevi> carasDetRevi = new ArrayList<>();
}