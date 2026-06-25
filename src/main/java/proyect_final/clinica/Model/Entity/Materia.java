package proyect_final.clinica.Model.Entity;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity

@Table (name="materia")
public class Materia  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_materia")
    private Long id_materia;


    @Column(name="nombre_materia", length = 100)
    private String nombreMateria;

    @Column(name="codigo_materia", length = 20 )
    private String codigoMateria;


    @ManyToOne
    @JoinColumn(name = "id_clinica", nullable = false)
    private Clinica clinica;    
    @Column(name = "usu_reg_mat")
    private Integer usuRegMat;

    @Column(name = "usu_mod_mat")
    private Integer usuModMat;

    @CreationTimestamp
    @Column(name = "fech_reg_mat", updatable = false)
    private LocalDateTime fechRegMat;

    @UpdateTimestamp
    @Column(name = "fech_mod_mat")
    private LocalDateTime fechModMat;
}