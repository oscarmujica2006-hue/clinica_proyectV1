package proyect_final.clinica.Model.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter@Getter
@Entity
@Table(name="periodo")

public class Periodo  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_periodo")
    private Long idPeriodo;

    @Column(name = "nombre_periodo", length = 50)
    private String nombrePeriodo;

    @Column(name="fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name="fecha_fin")
    private LocalDate fechaFin;
    @Column(name = "usu_reg_per")
    private Integer usuRegPer;

    @Column(name = "usu_mod_per")
    private Integer usuModPer;

    @CreationTimestamp
    @Column(name = "fech_reg_per", updatable = false)
    private LocalDateTime fechRegPer;

    @UpdateTimestamp
    @Column(name = "fech_mod_per")
    private LocalDateTime fechModPer;
}