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
    
    
    @ManyToOne
    @JoinColumn(name = "id_diagnostico_tratamiento")
    private DiagnosticoTratamiento diagnosticoTratamiento;
    
    @ManyToOne
    @JoinColumn(name = "id_radiografia")
    private Radiografia radiografia; 

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;
    
    @Column(name = "est_soli_radi", length = 50)  
    private String estado;  
    
    @Column(name = "motivo", length = 500)  
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