package proyect_final.clinica.Model.Entity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name="det_soli_radiografia")
public class DetalleSolicitudRadiografia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_det_soli_radiografia")
    private Long idDetSoliRadiografia;

     @ManyToOne
    @JoinColumn(name="id_solicitud_radiografia")
    private SolicitudRadiografia solicitudRadiografia;
    

    @ManyToOne
    @JoinColumn(name="id_diag_trat_diente")
    private DiagnosticoTratamientoDiente diagTratDiente;
}
