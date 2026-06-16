package proyect_final.clinica.Model.Entity;
import lombok.*;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tipo_paciente")
public class TipoPaciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_paciente")
    private Long idTipoPaciente;

    @Column(name = "nombre_tipo", nullable = false, unique = true, length = 20)
    private String nombreTipo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "edad_min")
    private Integer edadMin;

    @Column(name = "edad_max")
    private Integer edadMax;

}