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
public class Lote{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Long idLote;

    @Column(name = "numero_lote", nullable = false, unique = true, length = 50)
    private String numeroLote;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @ManyToOne
    @JoinColumn(name = "id_insumo", nullable = false)
    private Insumo insumo;

    @Column(name = "cantidad_inicial", nullable = false)
    private Integer cantidadInicial;

    @Column(name = "cantidad_disponible", nullable = false)
    private Integer cantidadDisponible;

    @Column(name = "origen", length = 20)
    private String origen;

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

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleLote> detallesLote = new ArrayList<>();
}