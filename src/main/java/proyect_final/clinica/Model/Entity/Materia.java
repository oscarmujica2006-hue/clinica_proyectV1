package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity

@Table (name="materia")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_materia")
    private Long id_materia;


    @Column(name="nombre_materia", length = 100)
    private String nombreMateria;

    @Column(name="codigo_materia", length = 20 )
    private String codigoMateria;


    @ManyToOne
    @JoinColumn(name = "id_clinica", nullable = false)
    private Clinica clinica;    
}
