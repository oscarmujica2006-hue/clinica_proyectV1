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
@Table(name = "examen_extra_oral")
public class ExamenExtraOral  {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_examen_extra_oral")
    private Long idExamenExtraOral;

    @Column( name="atm",  length = 100)
    private String atm;

    @Column(name = "ganglios_linfaticos", length = 100)
    private String gangliosLinfaticos;

    @Column(length = 100)
    private String respirador;

    @Column(name = "usu_reg_exaExt")
    private Integer usuRegExaExt;

    @Column(name = "usu_mod_exaExt")
    private Integer usuModExaExt;

    @CreationTimestamp
    @Column(name = "fech_reg_exaExt", updatable = false)
    private LocalDateTime fechRegExaExt;

    @UpdateTimestamp
    @Column(name = "fech_mod_exaExt")
    private LocalDateTime fechModExaExt;
}