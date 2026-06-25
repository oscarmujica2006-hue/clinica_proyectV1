package proyect_final.clinica.Model.Entity;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "detalle_acta")
public class DetalleActaEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_acta")
    private Long idDetalleActa;

    @ManyToOne
    @JoinColumn(name = "id_acta_entrega")
    private ActaEntrega actaEntrega;

    @ManyToOne
    @JoinColumn(name = "id_detalle_abastecimiento")
    private DetalleAbastecimiento detalleAbastecimiento;

    @Column(name = "cantidad_entregada")
    private Integer cantidadEntregada;
    
    @OneToOne(mappedBy = "detalleActaEntrega")
    private Lote lote;


    @Column(name = "usu_reg_detActa")
    private Integer usuRegDetActa;

    @Column(name = "usu_mod_detActa")
    private Integer usuModDetActa;

    @CreationTimestamp
    @Column(name = "fech_reg_detActa", updatable = false)
    private LocalDateTime fechRegDetActa;

    @UpdateTimestamp
    @Column(name = "fech_mod_detActa")
    private LocalDateTime fechModDetActa;
}