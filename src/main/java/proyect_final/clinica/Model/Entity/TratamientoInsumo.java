package proyect_final.clinica.Model.Entity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table  (name = "tratamiento_insumo")
public class TratamientoInsumo {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamiento_insumo")
    private Long idTratamientoInsumo;
    @ManyToOne
    @JoinColumn(name = "id_tratamiento", nullable = false)
    private Tratamiento tratamiento;

    @ManyToOne
    @JoinColumn(name = "id_insumo", nullable = false)
    private Insumo insumo;

    @Column(name = "cantidad_requerida", nullable = false)
    private Integer cantidadRequerida;
}
