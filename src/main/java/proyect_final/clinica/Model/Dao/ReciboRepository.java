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
    
    // Buscar recibos por consentimiento
    List<Recibo> findByConsentimiento_IdConsentimiento(Long idConsentimiento);
    
    // Buscar recibos por estado de pago
    List<Recibo> findByEstadoPago(String estadoPago);
    
    // Buscar recibos con saldo pendiente (monto_total > monto_pagado)
    @Query("SELECT r FROM Recibo r WHERE r.montoTotal > r.montoPagado")
    List<Recibo> findRecibosConSaldoPendiente();
    
    // Buscar recibos pendientes de un consentimiento
    @Query("SELECT r FROM Recibo r WHERE r.consentimiento.idConsentimiento = :idConsentimiento AND r.estadoPago != 'PAGADO'")
    List<Recibo> findRecibosPendientesByConsentimiento(@Param("idConsentimiento") Long idConsentimiento);
    
    // Buscar recibos por rango de fechas
    @Query("SELECT r FROM Recibo r WHERE r.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    List<Recibo> findRecibosByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                           @Param("fechaFin") LocalDateTime fechaFin);


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