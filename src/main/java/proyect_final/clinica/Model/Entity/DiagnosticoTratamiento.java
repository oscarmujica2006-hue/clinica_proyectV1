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

    @ManyToOne
    @JoinColumn(name = "id_diagnostico", nullable = false)
    private Diagnostico diagnostico;

    @ManyToOne
    @JoinColumn(name = "id_tratamiento", nullable = false)
    private Tratamiento tratamiento;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name="diente_afectado", length=50)
    private String dienteAfectado;  

    @Column(name = "usu_reg_diaTra")
    private Integer usuRegDiaTra;

    @Column(name = "usu_mod_diaTra")
    private Integer usuModDiaTra;

    @CreationTimestamp
    @Column(name = "fech_reg_diaTra", updatable = false)
    private LocalDateTime fechRegDiaTra;

    @UpdateTimestamp
    @Column(name = "fech_mod_diaTra")
    private LocalDateTime fechModDiaTra;

    @OneToMany(mappedBy = "diagnosticoTratamiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Consentimiento> consentimientos = new ArrayList<>();

    @OneToMany(mappedBy = "diagnosticoTratamiento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TratamientoRealizado> tratamientosRealizados = new ArrayList<>();
}