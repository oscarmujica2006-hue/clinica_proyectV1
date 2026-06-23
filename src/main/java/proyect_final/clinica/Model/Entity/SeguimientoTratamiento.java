package proyect_final.clinica.Model.Entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
@Table(name = "seguimiento_tratamiento")
public class SeguimientoTratamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento_tratamiento")
    private Long idSeguimientoTratamiento;

    @ManyToOne
    @JoinColumn(name = "id_consentimiento", nullable = false)
    private Consentimiento consentimiento;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "presion_arterial")
    private String presionArterial;

    @Column(name = "frecuencia_cardiaca")
    private String frecuenciaCardiaca;

    @Column(name = "frecuencia_respiratoria")
    private String frecuenciaRespiratoria;

    @Column(name = "temperatura")
    private String temperatura;

    @Column(name = "peso")
    private String peso;

    @Column(name = "subjetivo")
    private String subjetivo;

    @Column(name = "objetivo")
    private String objetivo;

    @Column(name = "analisis")
    private String analisis;

    @Column(name = "plan_accion")
    private String planAccion;
    @Column(name = "usu_reg_segTra", length = 100)
    private String usuRegSegTra;

    @Column(name = "usu_mod_segTra", length = 100)
    private String usuModSegTra;

    @CreationTimestamp
    @Column(name = "fech_reg_segTra", updatable = false)
    private LocalDateTime fechRegSegTra;

    @UpdateTimestamp
    @Column(name = "fech_mod_segTra")
    private LocalDateTime fechModSegTra;

    // Relación con detalles (un seguimiento tiene muchos detalles)
    @OneToMany(mappedBy = "seguimientoTratamiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleSeguimiento> detalles = new ArrayList<>();
}