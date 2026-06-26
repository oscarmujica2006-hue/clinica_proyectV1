package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "detalle_evolucion_clinica")
public class DetalleEvolucionClinica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDetalleEvolucion;
    
    @ManyToOne
    @JoinColumn(name = "id_evolucion_clinica")
    private EvolucionClinica evolucionClinica;
    
    // ✅ CORREGIDO: Nombre del campo y columna
    @ManyToOne
    @JoinColumn(name = "id_diagnostico_tratamiento_diente")
    private DiagnosticoTratamientoDiente diagnosticoTratamientoDiente;
    
    @Column(name = "numero_sesion")
    private Integer numeroSesion;
    
    @Column(name = "procedimiento_realizado", length = 500)
    private String procedimientoRealizado;
    
    @Column(name = "observaciones", length = 500)
    private String observaciones;
    
    @Column(name = "est_det_evo_clin", length = 50)
    private String estDetEvoClin;
    
    @Column(name = "usu_reg_det_evo")
    private Integer usuRegDetEvo;

    @Column(name = "usu_mod_det_evo")
    private Integer usuModDetEvo;

    @CreationTimestamp
    @Column(name = "fech_reg_det_evo", updatable = false)
    private LocalDateTime fechRegDetEvo;

    @UpdateTimestamp
    @Column(name = "fech_mod_det_evo")
    private LocalDateTime fechModDetEvo;
}