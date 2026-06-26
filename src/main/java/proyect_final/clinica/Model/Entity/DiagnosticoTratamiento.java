package proyect_final.clinica.Model.Entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "diagnostico_tratamiento")
public class DiagnosticoTratamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diag_trat")
    private Long idDiagTrat;

    @OneToOne
    @JoinColumn(name = "id_evolucion_clinica", nullable = false)
    private EvolucionClinica evolucionClinica;
    @ManyToOne
    @JoinColumn(name = "id_tratamiento", nullable = false)
    private Tratamiento tratamiento;

    @Column(name = "cantidad_planeada")
    private Integer cantidadPlaneada;
    
    @Column(name = "cantidad_realizada")
    private Integer cantidadRealizada;
    
    @Column(name = "cantidad_pendiente")
    private Integer cantidadPendiente;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "estado", length = 50)
    private String estado;  // PENDIENTE, EN_PROCESO, COMPLETADO, CANCELADO

    @Column(name = "usu_reg_dia_tra")
    private Integer usuRegDiaTra;

    @Column(name = "usu_mod_dia_tra")
    private Integer usuModDiaTra;

    @CreationTimestamp
    @Column(name = "fech_reg_dia_tra", updatable = false)
    private LocalDateTime fechRegDiaTra;

    @UpdateTimestamp
    @Column(name = "fech_mod_dia_tra")
    private LocalDateTime fechModDiaTra;

    // ✅ Relación con DiagnosticoTratamientoDiente
    @OneToMany(mappedBy = "diagnosticoTratamiento", cascade = CascadeType.ALL)
    private List<DiagnosticoTratamientoDiente> dientes = new ArrayList<>();

    // ✅ Relación con Consentimiento
    @OneToOne(mappedBy = "diagnosticoTratamiento")
    private Consentimiento consentimiento;

    // ✅ Relación con SolicitudRadiografia
    @OneToMany(mappedBy = "diagnosticoTratamiento")
    private List<SolicitudRadiografia> solicitudesRadiografia = new ArrayList<>();
}