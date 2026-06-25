package proyect_final.clinica.Model.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Getter
@Setter
@Entity

@Table(name = "persona")
@Inheritance(strategy = InheritanceType.JOINED)
public class Persona  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_persona;

    @Column(nullable = false, length = 50)
    private String nombre;


    @Column(nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(nullable = false, length = 50)
    private String apellidoMaterno;


    @Column(nullable = false)
    private Integer edad;
    
    @Column(nullable = false, length = 1)
    private Character sexo; // 'M' o 'F'


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

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Usuario> usuarios = new ArrayList<>();
}