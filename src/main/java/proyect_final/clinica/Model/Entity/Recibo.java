package proyect_final.clinica.Model.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "recibo")
public class Recibo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recibo")
    private Long idRecibo;
    
    @OneToOne
    @JoinColumn(name = "id_consentimiento")
    private Consentimiento consentimiento;
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @Column(name = "estado_pago", length = 20)
    private String estadoPago;  // PENDIENTE, PARCIAL, PAGADO, CANCELADO
    
    @Column(name = "subtotal_tratamientos")
    private Double subtotalTratamientos;
    
    @Column(name = "subtotal_radiografias")
    private Double subtotalRadiografias;
    
    @Column(name = "monto_total")
    private Double montoTotal;
    
    @Column(name = "monto_pagado")
    private Double montoPagado;  
    
    @Column(name = "saldo_pendiente")
    private Double saldoPendiente;  
    
    @OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleRecibo> detalles = new ArrayList<>();
    
    @OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PagoRecibo> pagos = new ArrayList<>();
    
    @Column(name = "usu_reg_rec")
    private Integer usuRegRec;
    
    @Column(name = "usu_mod_rec")
    private Integer usuModRec;
    
    @CreationTimestamp
    @Column(name = "fech_reg_rec", updatable = false)
    private LocalDateTime fechRegRec;
    
    @UpdateTimestamp
    @Column(name = "fech_mod_rec")
    private LocalDateTime fechModRec;
    
    // =============================================
    // MÉTODOS DE NEGOCIO
    // =============================================
    
    public void recalcularTotal() {
        double totalTratamientos = 0;
        double totalRadiografias = 0;
        
        if (detalles != null) {
            for (DetalleRecibo detalle : detalles) {
                double subtotal = detalle.getSubtotal() != null ? detalle.getSubtotal() : 0;
                if ("RADIOGRAFIA".equals(detalle.getTipoItem())) {
                    totalRadiografias += subtotal;
                } else {
                    totalTratamientos += subtotal;
                }
            }
        }
        
        this.subtotalTratamientos = totalTratamientos;
        this.subtotalRadiografias = totalRadiografias;
        this.montoTotal = totalTratamientos + totalRadiografias;
        
        this.saldoPendiente = this.montoTotal - (this.montoPagado != null ? this.montoPagado : 0);
        
        // Actualizar estado según pagos
        if (this.montoTotal == 0) {
            this.estadoPago = "PENDIENTE";
        } else if (this.montoPagado == null || this.montoPagado == 0) {
            this.estadoPago = "PENDIENTE";
        } else if (this.saldoPendiente == null || this.saldoPendiente <= 0) {
            this.estadoPago = "PAGADO";
        } else if (this.montoPagado > 0 && this.saldoPendiente > 0) {
            this.estadoPago = "PARCIAL";
        }
    }
    
    public void registrarPago(Double monto, String metodoPago) {
        if (monto == null || monto <= 0) {
            throw new RuntimeException("El monto del pago debe ser mayor a 0");
        }
        
        if (this.montoPagado == null) {
            this.montoPagado = 0.0;
        }
        
        if (this.montoPagado + monto > this.montoTotal) {
            throw new RuntimeException("El monto del pago excede el saldo pendiente");
        }
        
        this.montoPagado += monto;
        this.saldoPendiente = this.montoTotal - this.montoPagado;
        
        // Crear registro de pago
        PagoRecibo pago = new PagoRecibo();
        pago.setRecibo(this);
        pago.setMonto(monto);
        pago.setMetodoPago(metodoPago);
        pago.setFechaPago(LocalDateTime.now());
        
        if (this.pagos == null) {
            this.pagos = new ArrayList<>();
        }
        this.pagos.add(pago);
        
        // Actualizar estado
        if (this.saldoPendiente <= 0) {
            this.estadoPago = "PAGADO";
        } else {
            this.estadoPago = "PARCIAL";
        }
    }
    
    // 🔥 MÉTODO PARA VERIFICAR SI TIENE RADIOGRAFÍAS PENDIENTES
    public boolean tieneRadiografiasPendientes() {
        if (detalles == null) return false;
        
        for (DetalleRecibo detalle : detalles) {
            if ("RADIOGRAFIA".equals(detalle.getTipoItem()) && 
                detalle.getSolicitudRadiografia() != null &&
                !Boolean.TRUE.equals(detalle.getSolicitudRadiografia().getPagado())) {
                return true;
            }
        }
        return false;
    }
    
    // 🔥 MÉTODO PARA OBTENER EL ESTUDIANTE ASOCIADO
    public Estudiante getEstudianteAsociado() {
        if (consentimiento == null) return null;
        
        DiagnosticoTratamiento dt = consentimiento.getDiagnosticoTratamiento();
        if (dt == null) return null;
        
        EvolucionClinica ec = dt.getEvolucionClinica();
        if (ec == null) return null;
        
        Diagnostico d = ec.getDiagnostico();
        if (d == null) return null;
        
        Revision r = d.getRevision();
        if (r == null) return null;
        
        Consulta c = r.getConsulta();
        if (c == null) return null;
        
        return c.getEstudiante();
    }
}