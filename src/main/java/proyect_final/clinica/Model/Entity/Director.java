package proyect_final.clinica.Model.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;


@Getter
@Setter

@Entity
@Table(name = "usuario_clinica")
public class Director {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_clinica")
    private Long idUsuarioClinica;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name="fecha_ini",nullable=false)
    private LocalDate fechaInicio;


    @Column(name = "fecha_fin", nullable = false )
    private LocalDate fechaFin;

}