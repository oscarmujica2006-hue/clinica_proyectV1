package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "paciente")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    private Long idPaciente;

    @Column(name = "historial_clinico", unique = true, nullable = false, length = 50)
    private String historialClinico;

    @ManyToOne
    @JoinColumn(name = "id_persona", nullable = false)  
    private Persona persona;

    @Column(name = "lugar_nacimiento", nullable = false, length = 100)
    private String lugarNacimiento;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false, length = 50)
    private String ocupacion;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false)
    private String telefono; 

    @Column(name = "grado_Instruccion", nullable = false, length = 20)
    private String gradoInstruccion; // P, S, T, U

    @Column(name = "estado_civil", nullable = false, length = 20)
    private String estadoCivil; // S, C, V, D

    @Column(name = "naciones_originarias", length = 50)
    private String nacionesOriginarias;

    @Column(length = 50)
    private String idioma;

    @Column(nullable = false)
    private Integer ci;


    @ManyToOne
    @JoinColumn(name = "id_tipo_paciente", nullable = false)
    private TipoPaciente tipoPaciente;


}