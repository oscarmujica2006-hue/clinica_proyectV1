package proyect_final.clinica.Model.Entity;
import lombok.*;
import jakarta.persistence.*;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Setter
@Entity
@Table(name = "diente")
public class Diente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diente")
    private Long idDiente;

    @Column(name = "numero_diente", nullable = false, unique = true)
    private Integer numeroDiente;

    @Column(name = "nombre_diente", nullable = false, length = 100)
    private String nombreDiente;

    @Column(name = "usu_reg_die")
    private Integer usuRegDie;

    @Column(name = "usu_mod_die")
    private Integer usuModDie;

    @CreationTimestamp
    @Column(name = "fech_reg_die", updatable = false)
    private LocalDateTime fechRegDie;

    @UpdateTimestamp
    @Column(name = "fech_mod_die")
    private LocalDateTime fechModDie;


    @OneToMany(mappedBy = "diente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleRevision> detallesRevision = new ArrayList<>();

    @OneToMany(mappedBy = "diente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TratamientoRealizado> tratamientosRealizados = new ArrayList<>();
}