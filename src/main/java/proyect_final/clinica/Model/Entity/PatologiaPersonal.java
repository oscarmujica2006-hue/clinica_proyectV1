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
@Table(name = "patologiapersonal")
public class PatologiaPersonal  {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_patologiapersonal")
    private Long idPatologiaPersonal;
    
    @Column(name = "anemia")
    private Boolean anemia;

    @Column(name = "cardiopatias")
    private Boolean cardiopatias;

    @Column(name = "enf_gastricos")
    private Boolean enfGastricos;

    @Column(name = "hepatitis")
    private Boolean hepatitis;

    @Column(name = "tuberculosis")
    private Boolean tuberculosis;

    @Column(name = "asma")
    private Boolean asma;

    @Column(name = "diabetes_mel")
    private Boolean diabetesMel;

    @Column(name = "epilepsia")
    private Boolean epilepsia;

    @Column(name = "hipertension")
    private Boolean hipertension;

    @Column(name = "vih")
    private Boolean vih;

    @Column(name = "alergias")
    private Boolean alergias;

    @Column(name = "embarazo")
    private Boolean embarazo;

    @Column(name = "semana_embarazo")
    private Integer semanaEmbarazo;


    @Column(name = "otros")
    private String otros;

    @Column(name="ninguno")
    private Boolean ninguno;
    @Column(name = "usu_reg_patPer")
    private Integer usuRegPatPer;

    @Column(name = "usu_mod_patPer")
    private Integer usuModPatPer;

    @CreationTimestamp
    @Column(name = "fech_reg_patPer", updatable = false)
    private LocalDateTime fechRegPatPer;

    @UpdateTimestamp
    @Column(name = "fech_mod_patPer")
    private LocalDateTime fechModPatPer;
}