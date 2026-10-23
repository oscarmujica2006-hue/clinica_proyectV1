package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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
    @Column(name = "id_solicitud_radiografia")
    private Long idSolicitudRadiografia;
    
    @ManyToOne
    @JoinColumn(name = "id_diagnostico_tratamiento")
    private DiagnosticoTratamiento diagnosticoTratamiento;
    
    @ManyToOne
    @JoinColumn(name = "id_radiografia")
    private Radiografia radiografia;

    @OneToMany(mappedBy = "solicitudRadiografia")
    private List<DetalleSolicitudRadiografia> detalles;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @ManyToOne
    @JoinColumn(name = "id_estudiante")
    private Estudiante estudiante;
    
    @Column(name = "est_soli_radi", length = 50)  
    private String estado;  // PENDIENTE, APROBADA, RECHAZADA, REALIZADA
    
    @Column(name = "motivo", length = 500)  
    private String motivo;

    // 🔥 ========== NUEVOS CAMPOS PARA PAGO ==========
    
    @Column(name = "pagado")
    private Boolean pagado = false;
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @Column(name = "fecha_limite_pago")
    private LocalDateTime fechaLimitePago;
    
    @Column(name = "incluida_en_recibo")
    private Boolean incluidaEnRecibo = false;
    
    @Column(name = "id_recibo")
    private Long idRecibo;
    
    @Column(name = "motivo_rechazo", length = 500)
    private String motivoRechazo;
    
    
    @Column(name = "calidad", length = 50)
    private String calidad;  // EXCELENTE, BUENA, REGULAR, MALA, DEFECTUOSA
    
    @Column(name = "observaciones_calidad", length = 500)
    private String observacionesCalidad;
    
    
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
    
    // 🔥 ========== MÉTODOS AUXILIARES ==========
    
    public boolean isVencida() {
        return fechaLimitePago != null && 
               LocalDateTime.now().isAfter(fechaLimitePago) &&
               !Boolean.TRUE.equals(pagado);
    }
    
    public boolean isMalRealizada() {
        return "MALA".equals(calidad) || "DEFECTUOSA".equals(calidad);
    }
    
    public boolean isCalidadAceptable() {
        return "EXCELENTE".equals(calidad) || "BUENA".equals(calidad);
    }
    
    public boolean isPagada() {
        return Boolean.TRUE.equals(pagado);
    }
    
    public boolean isIncluidaEnRecibo() {
        return Boolean.TRUE.equals(incluidaEnRecibo);
    }
}