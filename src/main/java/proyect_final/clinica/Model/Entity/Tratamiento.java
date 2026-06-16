package proyect_final.clinica.Model.Entity;
import lombok.*;
import jakarta.persistence.*;
@Getter
@Setter

@Entity
@Table(name = "tratamiento")
public class Tratamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamiento")
    private Long idTratamiento;

    @OneToOne
    @JoinColumn(name = "id_tipo_tratamiento", nullable = false)
    private TipoTratamiento tipoTratamiento;

    @Column(name = "nombre_tratamiento", nullable = false, length = 100)
    private String nombreTratamiento;

    @Column(name="precio_tratamiento", nullable=false)
    private Double precioTratamiento;

    @Column(name = "descripcion_tratamiento", length = 255)
    private String descripcionTratamiento;

}
