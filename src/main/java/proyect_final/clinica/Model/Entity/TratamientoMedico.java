package proyect_final.clinica.Model.Entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

@Entity
@Table(name = "tratamientomedico")
public class TratamientoMedico  {
    
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

    @Column(name = "usu_reg_traMed", length = 100)
    private String usuRegTraMed;

    @Column(name = "usu_mod_traMed", length = 100)
    private String usuModTraMed;

    @CreationTimestamp
    @Column(name = "fech_reg_traMed", updatable = false)
    private LocalDateTime fechRegTraMed;

    @UpdateTimestamp
    @Column(name = "fech_mod_traMed")
    private LocalDateTime fechModTraMed;
}