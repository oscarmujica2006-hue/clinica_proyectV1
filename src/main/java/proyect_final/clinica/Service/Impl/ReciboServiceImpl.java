package proyect_final.clinica.Service.Impl;

import proyect_final.clinica.Model.Dao.ReciboRepository;
import proyect_final.clinica.Model.Dao.DetalleReciboRepository;
import proyect_final.clinica.Model.Dao.PagoReciboRepository;
import proyect_final.clinica.Model.Entity.Recibo;
import proyect_final.clinica.Model.Entity.DetalleRecibo;
import proyect_final.clinica.Model.Entity.PagoRecibo;
import proyect_final.clinica.Service.ReciboService;
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

    // =============================================
    // 🔥 REGISTRAR PAGO USANDO FUNCIÓN PostgreSQL
    // =============================================
    
    @Override
    public Recibo registrarPago(Long idRecibo, Double monto, String metodoPago, String observaciones) {
        try {
            // 🔥 Llamar a la función PostgreSQL
            String resultado = reciboRepository.registrarPago(
                idRecibo, 
                monto, 
                metodoPago, 
                observaciones,
                1  // idUsuario por defecto
            );
            
            System.out.println("✅ Resultado del pago: " + resultado);
            
            // Devolver el recibo actualizado
            return reciboRepository.findById(idRecibo)
                .orElseThrow(() -> new RuntimeException("Recibo no encontrado después del pago"));
            
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar pago: " + e.getMessage());
        }
    }

    // =============================================
    // ✅ MÉTODO PARA REGISTRAR PAGO CON USUARIO ESPECÍFICO
    // =============================================
    
    public Recibo registrarPagoConUsuario(Long idRecibo, Double monto, String metodoPago, 
                                          String observaciones, Integer idUsuario) {
        try {
            String resultado = reciboRepository.registrarPago(
                idRecibo, monto, metodoPago, observaciones, idUsuario
            );
            System.out.println("✅ Resultado del pago: " + resultado);
            
            return reciboRepository.findById(idRecibo)
                .orElseThrow(() -> new RuntimeException("Recibo no encontrado"));
            
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar pago: " + e.getMessage());
        }
    }

    // =============================================
    // VERIFICAR PAGOS VENCIDOS (tarea programada)
    // =============================================
    
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 * * * *")
    public void verificarPagosVencidos() {
        try {
            String resultado = reciboRepository.verificarPagosVencidosRadiografia();
            System.out.println("✅ Verificación de pagos vencidos: " + resultado);
        } catch (Exception e) {
            System.err.println("❌ Error al verificar pagos vencidos: " + e.getMessage());
        }
    }

    // =============================================
    // MÉTODOS EXISTENTES
    // =============================================
    
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

    @Override
    public Recibo findByConsentimientoId(Long idConsentimiento) {
        return reciboRepository.findByConsentimientoId(idConsentimiento);
    }

    @Override
    public Recibo findByEvolucionClinicaId(Long idEvolucionClinica) {
        if (idEvolucionClinica == null) {
            return null;
        }
        return reciboRepository.findByEvolucionClinicaId(idEvolucionClinica);
    }

    @Override
    public List<Recibo> findByConsentimientoIdList(Long idConsentimiento) {
        return reciboRepository.findByConsentimiento_IdConsentimiento(idConsentimiento);
    }

    @Override
    public List<Recibo> findRecibosPendientesByConsentimiento(Long idConsentimiento) {
        return reciboRepository.findRecibosPendientesByConsentimiento(idConsentimiento);
    }

    @Override
    public Recibo generarRecibo(String tipoItem, Long idItem, String concepto, 
                                Double precioUnitario, Long idEvolucionClinica, 
                                Long idConsentimiento) {
        // El trigger lo maneja, este método es opcional
        return null;
    }

    @Override
    public List<PagoRecibo> obtenerPagosPorRecibo(Long idRecibo) {
        return pagoReciboRepository.findByReciboId(idRecibo);
    }

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

    @Override
    public List<Recibo> findByEstudianteId(Long idEstudiante) {
        return reciboRepository.findByEstudianteId(idEstudiante);
    }

    @Override
    public Double sumPagosByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Double total = reciboRepository.sumPagosByFechaBetween(fechaInicio, fechaFin);
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