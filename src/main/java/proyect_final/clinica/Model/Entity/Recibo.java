package proyect_final.clinica.Model.Entity;
import lombok.Setter;
import lombok.Getter;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "recibo")
public class Recibo  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recibo")
    private Long idRecibo;

    @OneToOne
    @JoinColumn(name = "id_seguimiento_tratamiento", nullable = false)
    private SeguimientoTratamiento seguimiento;

    @Column(name = "monto_total", nullable = false)
    private Double montoTotal;
    
    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name="estado_pago", nullable = false )
    private String estadoPago;
    @Column(name = "usu_reg_rec")
    private Integer usuRegRec;

    @Column(name = "usu_mod_rec")
    private Integer usuModRec;

    @CreationTimestamp
    @Column(name = "fech_reg_rec", updatable = false)
    private LocalDateTime fechRegRec;

    @UpdateTimestamp
    @Column(name = "fech_mod_rec")
    private LocalDateTime fechModRec;
}