package proyect_final.clinica.Model.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

@Entity
@Table(name = "odontograma_foto")
public class OdontogramaFoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_odontograma_foto")
    private Long id_odontogramaFoto;

    @ManyToOne
    @JoinColumn(name = "id_consulta", nullable = false)
    private Consulta consulta;

    private String rutaArchivo;      // Ruta pública para acceder a la imagen
    private String nombreOriginal;    // Nombre original del archivo
    private String tipoContenido;     // image/jpeg, etc.
    private Long tamano;             

}
