package proyect_final.clinica.Model.Entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="turno")
public class Turno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turno")
    private Long idTurno;

    @Column(name="nombre_turno",nullable=false,length = 43)
    private String nombreTurno;

    @Column(name="hora_inicio",nullable=false)
    private String horaInicio;
    @Column(name="hora_fin",nullable=false)
    private String horaFin;
    @Column(name = "usu_reg_tur", length = 100)
    private Integer usuRegTur;

    @Column(name = "usu_mod_tur", length = 100)
    private Integer usuModTur;

    @CreationTimestamp
    @Column(name = "fech_reg_tur", updatable = false)
    private LocalDateTime fechRegTur;

    @UpdateTimestamp
    @Column(name = "fech_mod_tur")
    private LocalDateTime fechModTur;
}