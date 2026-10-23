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
    // 🔥 LLAMAR A FUNCIÓN: registrar pago
    // =============================================
    @Query(value = "SELECT fn_registrar_pago(:idRecibo, :monto, :metodoPago, :observaciones, :idUsuario)::text", 
           nativeQuery = true)
    String registrarPago(
        @Param("idRecibo") Long idRecibo,
        @Param("monto") Double monto,
        @Param("metodoPago") String metodoPago,
        @Param("observaciones") String observaciones,
        @Param("idUsuario") Integer idUsuario
    );
    
    // =============================================
    // 🔥 LLAMAR A FUNCIÓN: verificar pagos vencidos (radiografías)
    // =============================================
    @Query(value = "SELECT fn_verificar_pagos_vencidos_radiografia()", 
           nativeQuery = true)
    String verificarPagosVencidosRadiografia();
    
    // =============================================
    // MÉTODOS EXISTENTES
    // =============================================
    @Query("SELECT r FROM Recibo r WHERE r.consentimiento.idConsentimiento = :idConsentimiento")
    Recibo findByConsentimientoId(@Param("idConsentimiento") Long idConsentimiento);
    
    @Query("SELECT r FROM Recibo r WHERE r.consentimiento.idConsentimiento = :idConsentimiento")
    List<Recibo> findByConsentimiento_IdConsentimiento(@Param("idConsentimiento") Long idConsentimiento);
    
    @Query("SELECT r FROM Recibo r WHERE r.consentimiento.idConsentimiento = :idConsentimiento AND r.estadoPago != 'PAGADO'")
    List<Recibo> findRecibosPendientesByConsentimiento(@Param("idConsentimiento") Long idConsentimiento);
    
    // Fallback: recibo creado sin consentimiento (busca por evolución clínica)
    @Query(value = "SELECT * FROM recibo WHERE id_evolucion_clinica = :idEvolucionClinica", nativeQuery = true)
    Recibo findByEvolucionClinicaId(@Param("idEvolucionClinica") Long idEvolucionClinica);
    
    List<Recibo> findByEstadoPago(String estadoPago);
    
    @Query("SELECT r FROM Recibo r WHERE r.estadoPago IN ('PENDIENTE', 'PARCIAL') AND r.saldoPendiente > 0")
    List<Recibo> findRecibosConSaldoPendiente();
    
    @Query("SELECT r FROM Recibo r WHERE r.fechRegRec BETWEEN :inicio AND :fin")
    List<Recibo> findRecibosByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoRecibo p WHERE p.fechRegPago BETWEEN :inicio AND :fin")
    Double sumPagosByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT r FROM Recibo r " +
           "JOIN r.consentimiento c " +
           "JOIN c.diagnosticoTratamiento dt " +
           "JOIN dt.evolucionClinica ec " +
           "JOIN ec.diagnostico d " +
           "JOIN d.revision rev " +
           "JOIN rev.consulta con " +
           "WHERE con.paciente.idPaciente = :idPaciente")
    List<Recibo> findByPacienteId(@Param("idPaciente") Long idPaciente);

    // Recibos de un estudiante (por consentimiento o por la cadena de la consulta)
    @Query(value = "SELECT DISTINCT r.* FROM recibo r " +
           "LEFT JOIN consentimiento co ON co.id_consentimiento = r.id_consentimiento " +
           "LEFT JOIN diagnostico_tratamiento dt ON dt.id_diag_trat = co.id_diag_trat " +
           "LEFT JOIN evolucion_clinica ec ON ec.id_evolucion_clinica = COALESCE(dt.id_evolucion_clinica, r.id_evolucion_clinica) " +
           "LEFT JOIN diagnostico d ON d.id_diagnostico = ec.id_diagnostico " +
           "LEFT JOIN revision rev ON rev.id_revision = d.id_revision " +
           "LEFT JOIN consulta con ON con.id_consulta = rev.id_consulta " +
           "WHERE co.id_estudiante = :idEstudiante OR con.id_estudiante = :idEstudiante " +
           "ORDER BY r.id_recibo DESC", nativeQuery = true)
    List<Recibo> findByEstudianteId(@Param("idEstudiante") Long idEstudiante);
}