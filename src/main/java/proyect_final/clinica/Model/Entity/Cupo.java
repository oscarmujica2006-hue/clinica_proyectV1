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
@Table(name="cupo")
public class Cupo  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cupo")
    private Long idCupo;

    @ManyToOne
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @ManyToOne
    @JoinColumn(name = "id_tratamiento", nullable = false)
    private Tratamiento tratamiento;
    
    @Column(name = "cupos_disponibles")
    private Integer cuposDisponibles;

    @Column(name = "usu_reg_cup", length = 100)
    private String usuRegCup;

    @Column(name = "usu_mod_cup", length = 100)
    private String usuModCup;

    @CreationTimestamp
    @Column(name = "fech_reg_cup", updatable = false)
    private LocalDateTime fechRegCup;

    @UpdateTimestamp
    @Column(name = "fech_mod_cup")
    private LocalDateTime fechModCup;
}