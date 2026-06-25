package proyect_final.clinica.Model.Entity;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tipo_paciente")
public class TipoPaciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_paciente")
    private Long idTipoPaciente;

    @Column(name = "nombre_tipo", nullable = false, unique = true, length = 20)
    private String nombreTipo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "edad_min")
    private Integer edadMin;

    @Column(name = "edad_max")
    private Integer edadMax;
    @Column(name = "usu_reg_tipPac")
    private Integer usuRegTipPac;

    @Column(name = "usu_mod_tipPac")
    private Integer usuModTipPac;

    @CreationTimestamp
    @Column(name = "fech_reg_tipPac", updatable = false)
    private LocalDateTime fechRegTipPac;

    @UpdateTimestamp
    @Column(name = "fech_mod_tipPac")
    private LocalDateTime fechModTipPac;
}