package proyect_final.clinica.Model.Entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "actae")
public class ActaEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acta")
    private Long idActaEntrega;

    @Column(name = "numero_acta", unique = true)
    private String numeroActa;

    @ManyToOne
    @JoinColumn(name = "id_abastecimiento")
    private Abastecimiento abastecimiento;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @Column(name = "observacion")
    private String observacion;

    @Column(name = "usu_reg_acta", length = 100)
    private String usuRegActa;

    @Column(name = "usu_mod_acta", length = 100)
    private String usuModActa;

    @CreationTimestamp
    @Column(name = "fech_reg_acta", updatable = false)
    private LocalDateTime fechRegActa;

    @UpdateTimestamp
    @Column(name = "fech_mod_acta")
    private LocalDateTime fechModActa;

    @OneToMany(mappedBy = "actaEntrega")
    private List<DetalleActaEntrega> detalles;
}