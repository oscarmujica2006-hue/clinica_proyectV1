package proyect_final.clinica.Model.Entity;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="inscripcion_materia")

public class InscripcionMateria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "id_inscripcion_materia")
    private Long idInscripcionMateria;


    @ManyToOne
    @JoinColumn(name="estudiante_id")
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name="materia_id")
    private Materia materia;

    @ManyToOne
    @JoinColumn(name="periodo_id")
    private Periodo periodo;

    @Column (name="fecha_inscripcion")
    private LocalDate fechaInscripcion;
    @Column(name="estado_inscripcion")
    private String estadoInscripcion ;

}
