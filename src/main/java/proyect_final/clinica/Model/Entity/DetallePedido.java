package proyect_final.clinica.Model.Entity;

import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "detalle_pedido_insumo")
public class DetallePedido  {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_pedido")

    private Long idDetallePedidoInsumo;


    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)        
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "id_insumo", nullable = false)
    private Insumo insumo;

    @ManyToOne
    @JoinColumn(name = "id_unidad_medida", nullable = false)
    private UnidadMedida unidadMedida;

    @Column(name = "cantidad_pedida", nullable = false)
    private Integer cantidadPedida;


    @Column(name = "estado_det_pedido", nullable = false, length = 20 )
    private String estadoDetPedido;

    @Column(name = "usu_reg_detPedIns")
    private Integer usuRegDetPedIns;

    @Column(name = "usu_mod_detPedIns")
    private Integer usuModDetPedIns;

    @CreationTimestamp
    @Column(name = "fech_reg_detPedIns", updatable = false)
    private LocalDateTime fechRegDetPedIns;

    @UpdateTimestamp
    @Column(name = "fech_mod_detPedIns")
    private LocalDateTime fechModDetPedIns;
}