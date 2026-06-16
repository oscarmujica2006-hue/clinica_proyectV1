package proyect_final.clinica.Model.Entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name="insumo")

public class Insumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_insumo")
    private Long idInsumo;

    @Column(name="nombre_insumo",nullable=false,length = 43)
    private String nombreInsumo;

    @Column(name="unidad_base",nullable=false,length = 43 )
    private String unidadBase;

    @Column(name="concentracion",nullable=false,length = 43)
    private String concentracion;

    @Column(name="stock_total",nullable=false)
    private Integer stockTotal;

    @Column(name="ultima_actualizacion",nullable=false)
    private LocalDate ultimaActualizacion;

}
