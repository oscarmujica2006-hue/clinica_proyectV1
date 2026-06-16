package proyect_final.clinica.Model.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;


@Getter@Setter
@Entity
@Table(name="unidad_medida")

public class UnidadMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad")
    private Long idUnidadMedida;

    @Column(name = "nombre_unidad", nullable = false, length = 50)
    private String nombreUnidad;

    @Column(name = "descripcion_unidad", length = 255)
    private String descripcionUnidad;


}