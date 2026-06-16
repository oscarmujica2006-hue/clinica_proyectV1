package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "antecedentes_bucodentales")
public class AntecedentesBucodentales {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_antecedentes_bucodentales")
    private Long idAntecedentesBucodentales;

    @Column(name = "fecha_revision")
    private LocalDate fechaRevision;

    @Column(length = 100)
    private Boolean Fuma;

    @Column(length = 100)        
    private Boolean Bebe;

    @Column(length = 100)
    private String otrosHabitos;
}