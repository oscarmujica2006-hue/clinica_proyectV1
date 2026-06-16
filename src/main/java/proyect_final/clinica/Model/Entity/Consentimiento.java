package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter


@Entity
@Table(name = "consentimiento")
public class Consentimiento {
    
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

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "decision", length = 20)
    private String decision;

    @Column(name = "estado_consentimiento")
    private String estado ;
}