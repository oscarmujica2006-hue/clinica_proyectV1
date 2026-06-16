package proyect_final.clinica.Model.Entity;
import lombok.Setter;
import lombok.Getter;
import jakarta.persistence.*;


@Getter
@Setter

@Entity
@Table(name = "rayos_x")

public class RayosX {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rayos_x")
    private Long idRayosX;



    @Column(name = "nombre_rayo", nullable = false, length = 255)
    private String nombreRayo;

    @Column(name = "precio_rayo", nullable = false, length = 500)
    private Double precioRayo;
    
    
}
