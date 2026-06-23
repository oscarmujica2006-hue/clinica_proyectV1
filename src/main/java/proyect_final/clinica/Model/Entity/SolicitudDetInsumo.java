package proyect_final.clinica.Model.Entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@Entity
@Table(name = "soli_det_insumo")
public class SolicitudDetInsumo  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_soli_det_insumo")
    private Long idSoliDetInsumo;
    @ManyToOne
    @JoinColumn(name = "id_solicitud_insumo", nullable = false)
    private SolicitudInsumo solicitudInsumo;
    @ManyToOne
    @JoinColumn(name = "id_insumo", nullable = false)
    private Insumo insumo;

    @ManyToOne
    @JoinColumn(name = "id_docente", nullable = false)  
    private Docente docente;
    @ManyToOne
    @JoinColumn(name = "id_responsable_insumo", nullable = true)
    private EncargadoInsumo responsableInsumo;
    @Column(name = "cantidad_solicitada", nullable = false)
    private Integer cantidadSolicitada;

    @Column(name = "cantidad_entregada", nullable = false, length = 20)
    private Integer cantidadEntregada;

    @Column(name = "estado_det_insumo", nullable = false, length = 20)
    private String estadoSoliDetalle;

    @Column(name = "usu_reg_solDetIns", length = 100)
    private String usuRegSolDetIns;

    @Column(name = "usu_mod_solDetIns", length = 100)
    private String usuModSolDetIns;

    @CreationTimestamp
    @Column(name = "fech_reg_solDetIns", updatable = false)
    private LocalDateTime fechRegSolDetIns;

    @UpdateTimestamp
    @Column(name = "fech_mod_solDetIns")
    private LocalDateTime fechModSolDetIns;
}