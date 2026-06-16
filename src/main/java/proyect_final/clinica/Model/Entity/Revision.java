package proyect_final.clinica.Model.Entity;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "revision")
public class Revision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_revision")
    private Long idRevision;

    @ManyToOne
    @JoinColumn(name = "id_consulta", nullable = false, unique = true)
    private Consulta consulta;

    @Column(name = "fecha_revision", nullable = false)
    private LocalDate fechaRevision;

    @Column(name = "observaciones_generales")
    private String observacionesGenerales;

    @OneToMany(mappedBy = "revision", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleRevision> detallesRevision = new ArrayList<>();

    @OneToMany(mappedBy = "revision", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Diagnostico> diagnosticos = new ArrayList<>();
}