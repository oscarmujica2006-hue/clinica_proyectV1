package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity


@Table(name = "archivo")
public class Archivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_archivo")

    private Long idArchivo;

    @Column(name = "codigo_archivo", nullable = false, length = 100)
    private String codigoArchivo;


    @Column(name = "ubicacion_fisica", nullable = false, length = 200)
    private String ubicacionFisica;     
    
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)  
    private Paciente paciente;

}
