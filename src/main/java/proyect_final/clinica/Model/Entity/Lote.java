package proyect_final.clinica.Model.Entity;
import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "lote")
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Long idLote;

    @Column(name = "numero_lote")
    private String numeroLote;

    @ManyToOne
    @JoinColumn(name = "id_insumo")
    private Insumo insumo;

    @ManyToOne
    @JoinColumn(name = "id_detalle_acta")
    private DetalleActaEntrega detalleActaEntrega;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "cantidad_inicial")
    private Integer cantidadInicial;

    @Column(name = "cantidad_empaque")
    private Integer cantidadEmpaque;
    
    @Column(name = "unidad_por_empaque")
    private Integer unidadPorEmpaque;


    @Column(name = "cantidad_disponible")
    private Integer cantidadDisponible;

    @Column(name="estado_lote")
    private String estadoLote;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "usu_reg_lot", length = 100)
    private String usuRegLot;

    @Column(name = "usu_mod_lot", length = 100)
    private String usuModLot;

    @CreationTimestamp
    @Column(name = "fech_reg_lot", updatable = false)
    private LocalDateTime fechRegLot;

    @UpdateTimestamp
    @Column(name = "fech_mod_lot")
    private LocalDateTime fechModLot;

}