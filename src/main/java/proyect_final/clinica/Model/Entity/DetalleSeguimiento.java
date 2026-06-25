package proyect_final.clinica.Model.Entity;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
@Getter
@Setter
@Entity
@Table(name = "detalle_seguimiento")

public class DetalleSeguimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_seguimiento")
    private Long idDetalleSeguimiento;

    @ManyToOne
    @JoinColumn(name="id_seguimiento_tratamiento", nullable = false)
    private SeguimientoTratamiento seguimientoTratamiento;

    @ManyToOne
    @JoinColumn(name="id_radiografia", nullable = false)
    private Radiografia radiografia;

    @Column(name = "usu_reg_detSeg")
    private Integer usuRegDetSeg;

    @Column(name = "usu_mod_detSeg")
    private Integer usuModDetSeg;

    @CreationTimestamp
    @Column(name = "fech_reg_detSeg", updatable = false)
    private LocalDateTime fechRegDetSeg;

    @UpdateTimestamp
    @Column(name = "fech_mod_detSeg")
    private LocalDateTime fechModDetSeg;
}

