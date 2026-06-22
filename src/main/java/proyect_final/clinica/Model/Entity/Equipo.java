package proyect_final.clinica.Model.Entity;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Entity
@Table(name="equipo")

public class Equipo {
  @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equipo")
    private Long idEquipo;

    @Column(name = "nombre_equipo", nullable = false)  
    private String nombreEquipo;
    

    @Column(name="codigo_equipo",nullable=false,length = 43)
    private String codigoequipo;


    
}