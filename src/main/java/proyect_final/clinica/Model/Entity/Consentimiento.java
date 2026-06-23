package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


@Entity
@Table(name = "consentimiento")
public class Consentimiento  {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consentimiento")
    private Long idConsentimiento;

    @ManyToOne
    @JoinColumn(name = "id_diag_trat", nullable = false)
    private DiagnosticoTratamiento diagnosticoTratamiento;

    @ManyToOne
    @JoinColumn(name = "id_docente", nullable = false)
    private Docente docente;

    @ManyToOne
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    @Column(name = "fecha_consentimiento")
    private LocalDateTime fecha;

    @Column(name = "decision", length = 20)
    private String decision;

    @Column(name = "estado_consentimiento")
    private String estado ;
    @Column(name = "usu_reg_con", length = 100)
    private String usuRegCon;

    @Column(name = "usu_mod_con", length = 100)
    private String usuModCon;

    @CreationTimestamp
    @Column(name = "fech_reg_con", updatable = false)
    private LocalDateTime fechRegCon;

    @UpdateTimestamp
    @Column(name = "fech_mod_con")
    private LocalDateTime fechModCon;
}