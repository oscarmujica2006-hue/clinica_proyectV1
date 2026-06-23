package proyect_final.clinica.Model.Entity;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table  (name = "tratamiento_insumo")
public class TratamientoInsumo  {
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
    @Column(name = "usu_reg_traIns", length = 100)
    private String usuRegTraIns;

    @Column(name = "usu_mod_traIns", length = 100)
    private String usuModTraIns;

    @CreationTimestamp
    @Column(name = "fech_reg_traIns", updatable = false)
    private LocalDateTime fechRegTraIns;

    @UpdateTimestamp
    @Column(name = "fech_mod_traIns")
    private LocalDateTime fechModTraIns;
}

