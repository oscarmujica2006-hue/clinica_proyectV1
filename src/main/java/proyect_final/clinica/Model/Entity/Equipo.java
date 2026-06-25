package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "equipo")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private Long idEquipo;

    @Column(name = "codigo_equipo", length = 43, nullable = false)
    private String codigoEquipo;

    @Column(name = "nombre_equipo", length = 255, nullable = false)
    private String nombreEquipo;

    @Column(name="estado_equipo")
    private String estadoEquipo;

    // Auditoría
    @Column(name = "usu_reg_equ")
    private Integer usuRegEqu;

    @Column(name = "usu_mod_equ")
    private Integer usuModEqu;

    @CreationTimestamp
    @Column(name = "fech_reg_equ", updatable = false)
    private LocalDateTime fechRegEqu;

    @UpdateTimestamp
    @Column(name = "fech_mod_equ")
    private LocalDateTime fechModEqu;
}