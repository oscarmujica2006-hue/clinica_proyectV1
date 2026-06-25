package proyect_final.clinica.Model.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.*;
@Getter
@Setter


@Entity@Table(name = "solicitud_insumo")
public class SolicitudInsumo{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud_insumo")
    private Long idSolicitudInsumo;

    @OneToOne
    @JoinColumn(name = "id_diagnostico_tratamiento", nullable = false)
    private DiagnosticoTratamiento diagnosticoTratamiento;

    @Column(name = "fecha_solicitud", nullable = false, length = 100)
    private LocalDate fechaSolicitud;

    @Column(name = "estado_solicitud", nullable = false)
    private String estadoSolicitud;
    @Column(name = "usu_reg_solIns")
    private Integer usuRegSolIns;

    @Column(name = "usu_mod_solIns")
    private Integer usuModSolIns;

    @CreationTimestamp
    @Column(name = "fech_reg_solIns", updatable = false)
    private LocalDateTime fechRegSolIns;

    @UpdateTimestamp
    @Column(name = "fech_mod_solIns")
    private LocalDateTime fechModSolIns;
  
    @OneToMany(mappedBy = "solicitudInsumo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SolicitudDetInsumo> detalles;

}
