package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "id_diagnostico_tratamiento_diente")
    private DiagnosticoTratamientoDiente diagnosticoTratamientoDiente;

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
    
    
    @Column(name = "usu_reg_det_rec")
    private Integer usuRegDetRec;
    
    @Column(name = "usu_mod_det_rec")
    private Integer usuModDetRec;
    
    @CreationTimestamp
    @Column(name = "fech_reg_det_rec", updatable = false)
    private LocalDateTime fechRegDetRec;
    
    @UpdateTimestamp
    @Column(name = "fech_mod_det_rec")
    private LocalDateTime fechModDetRec;
    
    // =============================================
    // MÉTODOS AUXILIARES
    // =============================================
    
    /**
     * Verifica si este detalle es una radiografía
     */
    public boolean esRadiografia() {
        return "RADIOGRAFIA".equals(tipoItem);
    }
    
    /**
     * Verifica si este detalle es un tratamiento
     */
    public boolean esTratamiento() {
        return "TRATAMIENTO".equals(tipoItem);
    }
    
    /**
     * Obtiene el nombre del item (tratamiento o radiografía)
     */
    public String getNombreItem() {
        if (esTratamiento() && diagnosticoTratamientoDiente != null) {
            DiagnosticoTratamiento dt = diagnosticoTratamientoDiente.getDiagnosticoTratamiento();
            if (dt != null && dt.getTratamiento() != null) {
                return dt.getTratamiento().getNombreTratamiento();
            }
            return "Tratamiento";
        }
        
        if (esRadiografia() && solicitudRadiografia != null) {
            Radiografia r = solicitudRadiografia.getRadiografia();
            if (r != null) {
                return "Radiografía: " + r.getNombreRayo();
            }
            return "Radiografía";
        }
        
        return "Item desconocido";
    }
    
    /**
     * Obtiene el número de diente (si aplica)
     */
    public Integer getNumeroDiente() {
        if (esTratamiento() && diagnosticoTratamientoDiente != null) {
            return diagnosticoTratamientoDiente.getDiente();
        }
        return null;
    }
    
    /**
     * Verifica si la radiografía asociada está pagada
     */
    public boolean isRadiografiaPagada() {
        if (esRadiografia() && solicitudRadiografia != null) {
            return Boolean.TRUE.equals(solicitudRadiografia.getPagado());
        }
        return false;
    }
    
    /**
     * Obtiene el ID de la solicitud de radiografía (si aplica)
     */
    public Long getIdSolicitudRadiografia() {
        if (esRadiografia() && solicitudRadiografia != null) {
            return solicitudRadiografia.getIdSolicitudRadiografia();
        }
        return null;
    }
    
    /**
     * Calcula el subtotal automáticamente
     */
    public void calcularSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = cantidad * precioUnitario;
        }
    }
}