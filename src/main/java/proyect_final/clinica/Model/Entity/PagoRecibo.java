package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Entity
@Table(name = "pago_recibo")
public class PagoRecibo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_recibo")
    private Long idPagoRecibo;
    
    @ManyToOne
    @JoinColumn(name = "id_recibo", nullable = false)
    private Recibo recibo;
    
    @Column(name = "monto")
    private Double monto;
    
    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;  // EFECTIVO, TARJETA, TRANSFERENCIA, OTRO
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @Column(name = "observaciones", length = 500)
    private String observaciones;
    
    @CreationTimestamp
    @Column(name = "fech_reg_pago", updatable = false)
    private LocalDateTime fechRegPago;
}