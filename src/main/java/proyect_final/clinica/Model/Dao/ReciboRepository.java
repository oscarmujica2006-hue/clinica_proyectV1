package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Recibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReciboRepository extends JpaRepository<Recibo, Long> {
    
    // =============================================
    // ✅ MÉTODO PRINCIPAL - Buscar por Consentimiento (retorna UNO)
    // =============================================
    @Query("SELECT r FROM Recibo r WHERE r.consentimiento.idConsentimiento = :idConsentimiento")
    Recibo findByConsentimientoId(@Param("idConsentimiento") Long idConsentimiento);
    
    // =============================================
    // ✅ MÉTODO PARA LISTA - Buscar por Consentimiento (retorna VARIOS)
    // =============================================
    @Query("SELECT r FROM Recibo r WHERE r.consentimiento.idConsentimiento = :idConsentimiento")
    List<Recibo> findByConsentimiento_IdConsentimiento(@Param("idConsentimiento") Long idConsentimiento);
    
    // =============================================
    // ✅ MÉTODO PARA RECIBOS PENDIENTES POR CONSENTIMIENTO
    // =============================================
    @Query("SELECT r FROM Recibo r WHERE r.consentimiento.idConsentimiento = :idConsentimiento AND r.estadoPago != 'PAGADO'")
    List<Recibo> findRecibosPendientesByConsentimiento(@Param("idConsentimiento") Long idConsentimiento);
    
    // =============================================
    // ✅ MÉTODOS EXISTENTES
    // =============================================
    List<Recibo> findByEstadoPago(String estadoPago);
    
    @Query("SELECT r FROM Recibo r WHERE r.estadoPago IN ('PENDIENTE', 'PARCIAL') AND r.saldoPendiente > 0")
    List<Recibo> findRecibosConSaldoPendiente();
    
    @Query("SELECT r FROM Recibo r WHERE r.fechRegRec BETWEEN :inicio AND :fin")
    List<Recibo> findRecibosByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoRecibo p WHERE p.fechRegPago BETWEEN :inicio AND :fin")
    Double sumPagosByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COUNT(r) FROM Recibo r WHERE r.estadoPago = :estado")
    Long countByEstadoPago(@Param("estado") String estado);
    
    @Query("SELECT COALESCE(SUM(r.montoPagado), 0) FROM Recibo r WHERE r.fechRegRec BETWEEN :inicio AND :fin")
    Double sumMontoPagadoByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT r FROM Recibo r " +
           "JOIN r.consentimiento c " +
           "JOIN c.diagnosticoTratamiento dt " +
           "JOIN dt.evolucionClinica ec " +
           "JOIN ec.diagnostico d " +
           "JOIN d.revision rev " +
           "JOIN rev.consulta con " +
           "WHERE con.paciente.idPaciente = :idPaciente")
    List<Recibo> findByPacienteId(@Param("idPaciente") Long idPaciente);
}