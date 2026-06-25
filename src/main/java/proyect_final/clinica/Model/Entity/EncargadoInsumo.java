package proyect_final.clinica.Model.Entity;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name="encargado_insumo")
public class EncargadoInsumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encargado_insumo")
    private Long idEncargadoInsumo;
    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario ;
 

    @Column(name = "area_responsabilidad", nullable = false)
    private String area_responsabilidad;

    @Column(name="codigo_almacen",nullable=false,length = 43)
    private String codigo_almacen;

    @Column(name = "usu_reg_encIns")
    private Integer usuRegEncIns;

    @Column(name = "usu_mod_encIns")
    private Integer usuModEncIns;

    @CreationTimestamp
    @Column(name = "fech_reg_encIns", updatable = false)
    private LocalDateTime fechRegEncIns;

    @UpdateTimestamp
    @Column(name = "fech_mod_encIns")
    private LocalDateTime fechModEncIns;
}
