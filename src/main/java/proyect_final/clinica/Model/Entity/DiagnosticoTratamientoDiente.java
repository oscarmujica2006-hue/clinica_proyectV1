package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "diagnostico_tratamiento_diente")
public class DiagnosticoTratamientoDiente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDiagnosticoTratamientoDiente;
    
    @ManyToOne
    @JoinColumn(name = "id_diagnostico_tratamiento", nullable = false)
    private DiagnosticoTratamiento diagnosticoTratamiento;
    
    @Column(name = "diente")
    private Integer diente;
    
    @Column(name = "estado", length = 50)
    private String estado;  // PENDIENTE, REALIZADO, CANCELADO
    
    @Column(name = "observaciones", length = 500)
    private String observaciones;
    
    @Column(name = "usu_reg_dia_tra_die")
    private Integer usuRegDiaTraDie;
    
    @Column(name = "usu_mod_dia_tra_die")
    private Integer usuModDiaTraDie;
    
    @CreationTimestamp
    @Column(name = "fech_reg_dia_tra_die", updatable = false)
    private LocalDateTime fechRegDiaTraDie;
    
    @UpdateTimestamp
    @Column(name = "fech_mod_dia_tra_die")
    private LocalDateTime fechModDiaTraDie;
}