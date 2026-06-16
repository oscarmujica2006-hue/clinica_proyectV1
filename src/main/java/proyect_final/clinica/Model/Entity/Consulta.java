package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;   
import lombok.Setter;

@Getter
@Setter


@Entity
@Table(name = "consulta")
public class Consulta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    private Long idConsulta;


    // Relaciones con otras entidades
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "id_estudiante", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_persona", nullable = false)
    private Informante informante;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_examen_extra_oral")
    private ExamenExtraOral examenExtraOral;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_examen_intra_oral")
    private ExamenIntraOral examenIntraOral;
    

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "patologiapersonal")
    private PatologiaPersonal patologiaPersonal;



    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_antecedentes_bucodentales")
    private AntecedentesBucodentales antecedentesBucodentales;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_tratamiento_medico")
    private TratamientoMedico tratamientoMedico;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_antecedentes_higiene_oral")
    private AntecedentesHigieneOral antecedentesHigieneOral;


    @ManyToOne
    @JoinColumn(name = "id_prestamo_actual")
    private PrestamoActual prestamo;



    public void setPrestamo(PrestamoActual prestamo) {
    this.prestamo = prestamo;
    }
}