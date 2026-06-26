package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "solicitud_radiografia")
public class SolicitudRadiografia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolicitudRadiografia;
    
    // ✅ CORREGIDO: Nombre más claro
    @ManyToOne
    @JoinColumn(name = "id_diagnostico_tratamiento")
    private DiagnosticoTratamiento diagnosticoTratamiento;
    
    @ManyToOne
    @JoinColumn(name = "id_radiografia")
    private Radiografia radiografia;  // ✅ Cambiado de idRadiografia a radiografia

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;
    
    @Column(name = "estado", length = 50)  // ✅ Cambiado de est_soli_radio a estado
    private String estado;  // SOLICITADA, APROBADA, REALIZADA, CANCELADA
    
    @Column(name = "motivo", length = 500)  // ✅ Agregado campo motivo
    private String motivo;

    @Column(name = "usu_reg_sol_rad")
    private Integer usuRegSolRad;

    @Column(name = "usu_mod_sol_rad")
    private Integer usuModSolRad;

    @CreationTimestamp
    @Column(name = "fech_reg_sol_rad", updatable = false)
    private LocalDateTime fechRegSolRad;

    @UpdateTimestamp
    @Column(name = "fech_mod_sol_rad")
    private LocalDateTime fechModSolRad;
}