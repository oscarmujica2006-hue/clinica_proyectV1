package proyect_final.clinica.Model.Entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;


@Getter@Setter
@Entity
@Table(name="unidad_medida")

public class UnidadMedida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidad")
    private Long idUnidadMedida;

    @Column(name = "nombre_unidad", nullable = false, length = 50)
    private String nombreUnidad;

    @Column(name = "descripcion_unidad", length = 255)
    private String descripcionUnidad;

    @Column(name = "usu_reg_uniMed", length = 100)
    private String usuRegUniMed;

    @Column(name = "usu_mod_uniMed", length = 100)
    private String usuModUniMed;

    @CreationTimestamp
    @Column(name = "fech_reg_uniMed", updatable = false)
    private LocalDateTime fechRegUniMed;

    @UpdateTimestamp
    @Column(name = "fech_mod_uniMed")
    private LocalDateTime fechModUniMed;
}