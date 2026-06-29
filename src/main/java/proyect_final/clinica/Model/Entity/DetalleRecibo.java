package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "detalle_recibo")
public class DetalleRecibo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_recibo")
    private Long idDetalleRecibo;

    @ManyToOne
    @JoinColumn(name = "id_recibo", nullable = false)
    private Recibo recibo;

    // ✅ Para tratamientos (dientes)
    @ManyToOne
    @JoinColumn(name = "id_diagnostico_tratamiento_diente")
    private DiagnosticoTratamientoDiente diagnosticoTratamientoDiente;

    // ✅ Para radiografías
    @ManyToOne
    @JoinColumn(name = "id_solicitud_radiografia")
    private SolicitudRadiografia solicitudRadiografia;

    @Column(name = "tipo_item", length = 50)
    private String tipoItem;  // "TRATAMIENTO" o "RADIOGRAFIA"

    @Column(name = "cantidad")
    private Integer cantidad;

    @Column(name = "precio_unitario")
    private Double precioUnitario;

    @Column(name = "subtotal")
    private Double subtotal;
}