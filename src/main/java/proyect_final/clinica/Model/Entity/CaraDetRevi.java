package proyect_final.clinica.Model.Entity;

import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "cara_det_revi")
public class CaraDetRevi  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cara_det_revi")
    private Long idCaraDetRevi;

    @ManyToOne
    @JoinColumn(name = "id_detalle_revision", nullable = false)
    private DetalleRevision detalleRevision;

    @ManyToOne
    @JoinColumn(name = "id_cara_diente", nullable = false)
    private CaraDiente caraDiente;
    @Column(name = "usu_reg_carDetRev")
    private Integer usuRegCarDetRev;

    @Column(name = "usu_mod_carDetRev")
    private Integer usuModCarDetRev;

    @CreationTimestamp
    @Column(name = "fech_reg_carDetRev", updatable = false)
    private LocalDateTime fechRegCarDetRev;

    @UpdateTimestamp
    @Column(name = "fech_mod_carDetRev")
    private LocalDateTime fechModCarDetRev;
}