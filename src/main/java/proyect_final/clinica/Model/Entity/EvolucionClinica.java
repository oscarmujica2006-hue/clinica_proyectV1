package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "evolucion_clinica")
public class EvolucionClinica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvolucionClinica;
    
    @ManyToOne
    @JoinColumn(name = "id_diagnostico")
    private Diagnostico diagnostico;
    
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;
    
    @Column(name = "subjetivo", length = 1000)
    private String subjetivo;
    
    @Column(name = "objetivo", length = 1000)
    private String objetivo;
    
    @Column(name = "analisis", length = 1000)
    private String analisis;
    
    @Column(name = "plan_accion", length = 1000)
    private String planAccion;
    
    @Column(name = "presion_arterial", length = 20)
    private String presionArterial;
    
    @Column(name = "frecuencia_cardiaca")
    private Integer frecuenciaCardiaca;
    
    @Column(name = "frecuencia_respiratoria")
    private Integer frecuenciaRespiratoria;
    
    @Column(name = "temperatura")
    private Double temperatura;
    
    @Column(name = "peso")
    private Double peso;
    
    @Column(name = "tipo_registro", length = 20)
    private String tipoRegistro;
    
    @ManyToOne
    @JoinColumn(name = "id_evolucion_padre")
    private EvolucionClinica evolucionPadre;
    

    
    @Column(name = "usu_reg_evo_cli")
    private Integer usuRegEvoCli;

    @Column(name = "usu_mod_evo_cli")
    private Integer usuModEvoCli;

    @Column(name = "fech_reg_evo_cli")
    private LocalDateTime fechRegEvoCli;

    @Column(name = "fech_mod_evo_cli")
    private LocalDateTime fechModEvoCli;
}