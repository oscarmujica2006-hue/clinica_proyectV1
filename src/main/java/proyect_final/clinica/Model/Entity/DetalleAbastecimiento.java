package proyect_final.clinica.Model.Entity;

import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "detalle_abastecimiento")
public class DetalleAbastecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_abastecimiento")
    private Long idDetalleAbastecimiento;

    @ManyToOne
    @JoinColumn(name = "id_abastecimiento")
    private Abastecimiento abastecimiento;

    @ManyToOne
    @JoinColumn(name = "id_detalle_pedido")
    private DetallePedido detallePedido;

   
    @Column(name = "cantidad_comprometida")
    private Integer cantidadComprometida;

    @Column(name = "cantidad_recibida")
    private Integer cantidadRecibida;
    @Column(name = "cantidad_restante")
    private Integer cantidadRestante;

    @Column(name = "costo_unitario")
    private BigDecimal costoUnitario;

    @Column(name = "costo_total")
    private BigDecimal costoTotal;

    @Column(name = "estado_detalle")
    private String estadoDetalle;

    @Column(name = "usu_reg_detAbas", length = 100)
    private Integer usuRegDetAbas;

    @Column(name = "usu_mod_detAbas", length = 100)
    private Integer usuModDetAbas;

    @CreationTimestamp
    @Column(name = "fech_reg_detAbas", updatable = false)
    private LocalDateTime fechRegDetAbas;
    
    @UpdateTimestamp
    @Column(name = "fech_mod_detAbas")
    private LocalDateTime fechModDetAbas;


}