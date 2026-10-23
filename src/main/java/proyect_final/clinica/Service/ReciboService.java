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
    
    // ==================== ✅ MÉTODOS POR CONSENTIMIENTO ====================
    Recibo findByConsentimientoId(Long idConsentimiento);          // Retorna UNO (para triggers)
    Recibo findByEvolucionClinicaId(Long idEvolucionClinica);       // Fallback si no hay consentimiento
    List<Recibo> findByConsentimientoIdList(Long idConsentimiento); // Retorna VARIOS (para listados)
    List<Recibo> findRecibosPendientesByConsentimiento(Long idConsentimiento);
    
    // ==================== GENERAR RECIBO (OPCIONAL) ====================
    Recibo generarRecibo(String tipoItem, Long idItem, String concepto, 
                         Double precioUnitario, Long idEvolucionClinica, 
                         Long idConsentimiento);
    
    // ==================== PAGOS ====================
    Recibo registrarPago(Long idRecibo, Double monto, String metodoPago, String observaciones);
    List<PagoRecibo> obtenerPagosPorRecibo(Long idRecibo);
    
    // ==================== CONSULTAS ====================
    List<Recibo> findByEstado(String estado);
    List<Recibo> findRecibosConSaldoPendiente();
    List<Recibo> findRecibosByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    List<Recibo> findByPacienteId(Long idPaciente);
    List<Recibo> findByEstudianteId(Long idEstudiante);
    
    // ==================== ESTADÍSTICAS ====================
    Double sumPagosByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Recibo actualizarEstadoPago(Long idRecibo, String nuevoEstado);
}