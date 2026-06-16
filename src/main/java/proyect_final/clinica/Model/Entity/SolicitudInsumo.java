package proyect_final.clinica.Model.Entity;
import java.time.LocalDate;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;
@Getter
@Setter


@Entity@Table(name = "solicitud_insumo")
public class SolicitudInsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud_insumo")
    private Long idSolicitudInsumo;

    @OneToOne
    @JoinColumn(name = "consentimiento", nullable = false)
    private Consentimiento consentimiento;

    @Column(name = "fecha_solicitud", nullable = false, length = 100)
    private LocalDate fechaSolicitud;

    @Column(name = "estado_solicitud", nullable = false)
    private String estadoSolicitud;
    
    @OneToMany(mappedBy = "solicitudInsumo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SolicitudDetInsumo> detalles;

}
