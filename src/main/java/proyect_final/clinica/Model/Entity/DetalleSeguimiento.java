package proyect_final.clinica.Model.Entity;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
@Getter
@Setter
@Entity
@Table(name = "detalle_seguimiento")

public class DetalleSeguimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_seguimiento")
    private Long idDetalleSeguimiento;

    @ManyToOne
    @JoinColumn(name="id_seguimiento_tratamiento", nullable = false)
    private SeguimientoTratamiento seguimientoTratamiento;

    @ManyToOne
    @JoinColumn(name="id_rayos_x", nullable = false)
    private RayosX rayosX;


}
