package proyect_final.clinica.Model.Entity;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "revision")
public class Revision  {
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
    @Column(name = "usu_reg_rev", length = 100)
    private String usuRegRev;

    @Column(name = "usu_mod_rev", length = 100)
    private String usuModRev;

    @CreationTimestamp
    @Column(name = "fech_reg_rev", updatable = false)
    private LocalDateTime fechRegRev;

    @UpdateTimestamp
    @Column(name = "fech_mod_rev")
    private LocalDateTime fechModRev;

    @OneToMany(mappedBy = "revision", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleRevision> detallesRevision = new ArrayList<>();

    @OneToMany(mappedBy = "revision", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Diagnostico> diagnosticos = new ArrayList<>();
}