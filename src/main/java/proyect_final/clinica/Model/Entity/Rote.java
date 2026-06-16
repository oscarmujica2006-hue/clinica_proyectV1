package proyect_final.clinica.Model.Entity;
import lombok.*;
import jakarta.persistence.*;
@Setter
@Getter
@Entity
@Table(name="rote")



public class Rote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rote")
    private Long idRote; 
    
    @Column(name="nombre_rote", length = 100)
    private String nombreRote;

}
