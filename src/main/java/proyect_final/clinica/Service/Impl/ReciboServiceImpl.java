package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.ReciboRepository;
import proyect_final.clinica.Model.Dao.DetalleReciboRepository;
import proyect_final.clinica.Model.Dao.PagoReciboRepository;
import proyect_final.clinica.Model.Entity.Recibo;
import proyect_final.clinica.Model.Entity.DetalleRecibo;
import proyect_final.clinica.Model.Entity.PagoRecibo;
import proyect_final.clinica.Model.Entity.DiagnosticoTratamientoDiente;
import proyect_final.clinica.Model.Entity.SolicitudRadiografia;
import proyect_final.clinica.Model.Entity.Consentimiento;
import proyect_final.clinica.Service.ReciboService;
import proyect_final.clinica.Service.DiagnosticoTratamientoDienteService;
import proyect_final.clinica.Service.SolicitudRadiografiaService;
import proyect_final.clinica.Service.ConsentimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReciboServiceImpl implements ReciboService {

    @Autowired
    private ReciboRepository reciboRepository;
    
    @Autowired
    private DetalleReciboRepository detalleReciboRepository;
    
    @Autowired
    private PagoReciboRepository pagoReciboRepository;
    
    @Autowired
    private DiagnosticoTratamientoDienteService dientePlanService;
    
    @Autowired
    private SolicitudRadiografiaService solicitudRadiografiaService;
    
    @Autowired
    private ConsentimientoService consentimientoService;

    // ==================== CRUD BÁSICO ====================
    
    @Override
    public List<Recibo> listarTodos() {
        return reciboRepository.findAll();
    }

    @Override
    public Optional<Recibo> obtenerPorId(Long id) {
        return reciboRepository.findById(id);
    }

    @Override
    public Recibo guardar(Recibo recibo) {
        return reciboRepository.save(recibo);
    }

    @Override
    public void eliminar(Long id) {
        reciboRepository.deleteById(id);
    }

    // ==================== ✅ MÉTODO PARA TRIGGERS - Retorna UNO ====================
    @Override
    public Recibo findByConsentimientoId(Long idConsentimiento) {
        try {
            return reciboRepository.findByConsentimientoId(idConsentimiento);
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== ✅ MÉTODO PARA LISTA - Retorna VARIOS ====================
    @Override
    public List<Recibo> findByConsentimientoIdList(Long idConsentimiento) {
        try {
            return reciboRepository.findByConsentimiento_IdConsentimiento(idConsentimiento);
        } catch (Exception e) {
            return List.of(); // Retorna lista vacía si no encuentra
        }
    }

    // ==================== ✅ MÉTODO PARA RECIBOS PENDIENTES ====================
    @Override
    public List<Recibo> findRecibosPendientesByConsentimiento(Long idConsentimiento) {
        try {
            return reciboRepository.findRecibosPendientesByConsentimiento(idConsentimiento);
        } catch (Exception e) {
            return List.of();
        }
    }

    // ==================== GENERAR RECIBO (OPCIONAL) ====================
    @Override
    public Recibo generarRecibo(
            String tipoItem,
            Long idItem,
            String concepto,
            Double precioUnitario,
            Long idEvolucionClinica,
            Long idConsentimiento) {
        
        // Este método ahora es opcional porque los triggers hacen el trabajo
        // Pero lo mantenemos para compatibilidad
        
        // 1. Obtener el consentimiento
        Consentimiento consentimiento = consentimientoService.obtenerPorId(idConsentimiento)
            .orElseThrow(() -> new RuntimeException("Consentimiento no encontrado"));
        
        // 2. Buscar recibo existente por consentimiento
        Recibo recibo = reciboRepository.findByConsentimientoId(idConsentimiento);
        
        if (recibo == null) {
            recibo = new Recibo();
            recibo.setConsentimiento(consentimiento);
            recibo.setFechaPago(LocalDateTime.now());
            recibo.setEstadoPago("PENDIENTE");
            recibo.setMontoPagado(0.0);
            recibo.setSaldoPendiente(0.0);
            recibo = reciboRepository.save(recibo);
        }
        
        // 3. Verificar que no exista duplicado
        boolean existe = false;
        if ("TRATAMIENTO".equals(tipoItem)) {
            DetalleRecibo existente = detalleReciboRepository
                .findByDiagnosticoTratamientoDienteId(idItem);
            existe = existente != null;
        } else if ("RADIOGRAFIA".equals(tipoItem)) {
            DetalleRecibo existente = detalleReciboRepository
                .findBySolicitudRadiografiaId(idItem);
            existe = existente != null;
        }
        
        if (existe) {
            throw new RuntimeException("Este item ya está registrado en el recibo");
        }
        
        // 4. Crear detalle
        DetalleRecibo detalle = new DetalleRecibo();
        detalle.setRecibo(recibo);
        detalle.setTipoItem(tipoItem);
        detalle.setCantidad(1);
        detalle.setPrecioUnitario(precioUnitario);
        detalle.setSubtotal(precioUnitario);
        
        // 5. Asociar el item correspondiente
        if ("TRATAMIENTO".equals(tipoItem)) {
            DiagnosticoTratamientoDiente dientePlan = dientePlanService.obtenerPorId(idItem)
                .orElseThrow(() -> new RuntimeException("Diente del plan no encontrado"));
            detalle.setDiagnosticoTratamientoDiente(dientePlan);
        } else if ("RADIOGRAFIA".equals(tipoItem)) {
            SolicitudRadiografia solicitud = solicitudRadiografiaService.obtenerPorId(idItem)
                .orElseThrow(() -> new RuntimeException("Solicitud de radiografía no encontrada"));
            detalle.setSolicitudRadiografia(solicitud);
        }
        
        // 6. Guardar detalle
        detalleReciboRepository.save(detalle);
        recibo.getDetalles().add(detalle);
        
        // 7. Recalcular total
        recibo.recalcularTotal();
        return reciboRepository.save(recibo);
    }

    // ==================== PAGOS ====================
    
    @Override
    public Recibo registrarPago(Long idRecibo, Double monto, String metodoPago, String observaciones) {
        Recibo recibo = reciboRepository.findById(idRecibo)
            .orElseThrow(() -> new RuntimeException("Recibo no encontrado"));
        
        // Validar que el monto no exceda el saldo pendiente
        Double saldoPendiente = recibo.getSaldoPendiente() != null ? recibo.getSaldoPendiente() : recibo.getMontoTotal();
        if (monto > saldoPendiente) {
            throw new RuntimeException("El monto del pago excede el saldo pendiente. Saldo: " + saldoPendiente);
        }
        
        // Registrar el pago en el recibo
        recibo.registrarPago(monto, metodoPago);
        
        // Guardar el pago con observaciones
        if (recibo.getPagos() != null && !recibo.getPagos().isEmpty()) {
            PagoRecibo ultimoPago = recibo.getPagos().get(recibo.getPagos().size() - 1);
            ultimoPago.setObservaciones(observaciones);
            pagoReciboRepository.save(ultimoPago);
        }
        
        return reciboRepository.save(recibo);
    }

    @Override
    public List<PagoRecibo> obtenerPagosPorRecibo(Long idRecibo) {
        return pagoReciboRepository.findByReciboId(idRecibo);
    }

    // ==================== CONSULTAS ====================
    
    @Override
    public List<Recibo> findByEstado(String estado) {
        return reciboRepository.findByEstadoPago(estado);
    }

    @Override
    public List<Recibo> findRecibosConSaldoPendiente() {
        return reciboRepository.findRecibosConSaldoPendiente();
    }

    @Override
    public List<Recibo> findRecibosByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return reciboRepository.findRecibosByFechaBetween(fechaInicio, fechaFin);
    }

    @Override
    public List<Recibo> findByPacienteId(Long idPaciente) {
        return reciboRepository.findByPacienteId(idPaciente);
    }

    // ==================== ESTADÍSTICAS ====================
    
    @Override
    public Double sumPagosByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Double total = pagoReciboRepository.sumMontoByFechaBetween(fechaInicio, fechaFin);
        return total != null ? total : 0.0;
    }

    @Override
    public Recibo actualizarEstadoPago(Long idRecibo, String nuevoEstado) {
        Recibo recibo = reciboRepository.findById(idRecibo)
            .orElseThrow(() -> new RuntimeException("Recibo no encontrado"));
        recibo.setEstadoPago(nuevoEstado);
        return reciboRepository.save(recibo);
    }
}