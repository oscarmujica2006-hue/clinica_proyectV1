package proyect_final.clinica.Model.Entity;
import lombok.*;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name="encargado_insumo")
public class EncargadoInsumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encargado_insumo")
    private Long idEncargadoInsumo;
    @OneToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario ;
 

    @Column(name = "area_responsabilidad", nullable = false)
    private String area_responsabilidad;

    @Column(name="codigo_almacen",nullable=false,length = 43)
    private String codigo_almacen;

}
