package proyect_final.clinica.Model.Entity;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
@Getter
@Setter

@Entity
@Table(name = "tipo_tratamiento")


public class TipoTratamiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_tratamiento")
    private Long idTipoTratamiento;


    @OneToOne
    @JoinColumn(name = "id_clinica", nullable = false)
    private Clinica clinica;
    @Column(name = "nombre_tipo", nullable = false, length = 100)
    private String nombreTipo;

    @Column(name = "descripcion_tipo", length = 255)
    private String descripcionTipo;
    
}
