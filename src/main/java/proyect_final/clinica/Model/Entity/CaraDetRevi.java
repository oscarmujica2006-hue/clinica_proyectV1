package proyect_final.clinica.Model.Entity;

import lombok.*;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "cara_det_revi")
public class CaraDetRevi {
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
}