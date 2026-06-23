package proyect_final.clinica.Model.Entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "detalle_lote")
public class DetalleLote  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_det_lote")
    private Long idDetLote;

    @ManyToOne
    @JoinColumn(name = "id_detalle_pedido", nullable = false)
    private DetallePedido detallePedido;

    @ManyToOne
    @JoinColumn(name = "id_lote", nullable = false)
    private Lote lote;

    @Column(name = "cantidad_asignada", nullable = false)
    private Integer cantidadAsignada;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDate fechaAsignacion;

    @Column(name = "observaciones")
    private String observaciones;
    @Column(name = "usu_reg_detLot", length = 100)
    private String usuRegDetLot;

    @Column(name = "usu_mod_detLot", length = 100)
    private String usuModDetLot;

    @CreationTimestamp
    @Column(name = "fech_reg_detLot", updatable = false)
    private LocalDateTime fechRegDetLot;
    
    @UpdateTimestamp
    @Column(name = "fech_mod_detLot")
    private LocalDateTime fechModDetLot;
}