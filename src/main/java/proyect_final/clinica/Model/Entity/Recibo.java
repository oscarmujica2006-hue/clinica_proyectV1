// Recibo.java - CORREGIDO
package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "recibo")
public class Recibo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecibo;
    
    // ✅ CORRECTO: El nombre debe coincidir con mappedBy
    @OneToOne
    @JoinColumn(name = "id_evolucion_clinica")
    private EvolucionClinica evolucionClinica;  // ← Mismo nombre que en mappedBy
    
    @Column(name = "fecha")
    private LocalDateTime fecha;
    
    @Column(name = "estado_pago", length = 20)
    private String estadoPago;  // PAGADO, PENDIENTE, CANCELADO
    
    @Column(name = "subtotal_tratamientos")
    private Double subtotalTratamientos;
    
    @Column(name = "subtotal_radiografias")
    private Double subtotalRadiografias;
    
    @Column(name = "monto_total")
    private Double montoTotal;
    
    // Auditoría
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