package proyect_final.clinica.Model.Entity;

import lombok.*;
import jakarta.persistence.*;
import java.util.List;
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

    @ManyToOne
    @JoinColumn(name = "id_diagnostico", nullable = false)
    private Diagnostico diagnostico;

    @ManyToOne
    @JoinColumn(name = "id_tratamiento", nullable = false)
    private Tratamiento tratamiento;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "requiere_consentimiento")
    private Boolean requiereConsentimiento;

    @OneToMany(mappedBy = "diagnosticoTratamiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Consentimiento> consentimientos = new ArrayList<>();

    @OneToMany(mappedBy = "diagnosticoTratamiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TratamientoRealizado> tratamientosRealizados = new ArrayList<>();
}