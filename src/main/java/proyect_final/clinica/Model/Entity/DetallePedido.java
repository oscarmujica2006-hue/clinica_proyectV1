package proyect_final.clinica.Model.Entity;

import lombok.*;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "detalle_pedido_insumo")
public class DetallePedido {
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

    @Column(name = "cantidad_recibida")
    private Integer cantidadRecibida;

    @Column(name="saldo_pendiente")
    private Integer saldoPendiente;

    @Column(name = "estado_det_pedido", nullable = false, length = 20 )
    private String estadoDetPedido;

}


