package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Recibo;
import proyect_final.clinica.Model.Entity.PagoRecibo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReciboService {
    
    // ==================== CRUD BÁSICO ====================
    List<Recibo> listarTodos();
    
    Optional<Recibo> obtenerPorId(Long id);
    
    Recibo guardar(Recibo recibo);
    
    void eliminar(Long id);
    
    // ==================== GENERAR RECIBO ====================
    Recibo generarRecibo(
        String tipoItem,
        Long idItem,
        String concepto,
        Double precioUnitario,
        Long idEvolucionClinica,
        Long idConsentimiento
    );
    
    // ==================== PAGOS ====================
    Recibo registrarPago(Long idRecibo, Double monto, String metodoPago, String observaciones);
    
    List<PagoRecibo> obtenerPagosPorRecibo(Long idRecibo);
    
    // ==================== CONSULTAS ====================
    List<Recibo> findByConsentimientoId(Long idConsentimiento);
    
    List<Recibo> findByEstado(String estado);
    
    List<Recibo> findRecibosConSaldoPendiente();
    
    List<Recibo> findRecibosPendientesByConsentimiento(Long idConsentimiento);
    
    List<Recibo> findRecibosByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);



    List<Recibo> findByPacienteId(Long idPaciente);
    
    // ==================== ESTADÍSTICAS ====================
    Double sumPagosByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    Recibo actualizarEstadoPago(Long idRecibo, String nuevoEstado);
}