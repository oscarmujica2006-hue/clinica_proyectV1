package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

@Entity
@Table(name = "tratamientomedico")
public class TratamientoMedico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamientomedico")
    private Long idTratamientoMedico;

    @Column(name = "tratamiento_medico")
    private String tratamientoMedico;

    @Column(name = "recibe_algun_medicamento")
    private String recibeAlgunMedicamento;

    @Column(name = "tuvo_hemorragia_dental")
    private Boolean tuvoHemorragiaDental;

    @Column(name = "especifique", length = 50)
    private String especifiqueHemorragia;

}