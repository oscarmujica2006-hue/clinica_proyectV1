package proyect_final.clinica.Model.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="turno")
public class Turno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turno")
    private Long idTurno;

    @Column(name="nombre_turno",nullable=false,length = 43)
    private String nombreTurno;

    @Column(name="hora_inicio",nullable=false)
    private String horaInicio;
    @Column(name="hora_fin",nullable=false)
    private String horaFin;
}
