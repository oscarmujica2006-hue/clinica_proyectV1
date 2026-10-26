package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.SolicitudRadiografia;
import proyect_final.clinica.Model.Dto.RadiografiaPorClinicaDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRadiografiaRepository extends JpaRepository<SolicitudRadiografia, Long> {

    @Query("SELECT new proyect_final.clinica.Model.Dto.RadiografiaPorClinicaDTO(" +
           "c.idClinica, c.nombreClinica, r.nombreRayo, COUNT(sr.idSolicitudRadiografia)) " +
           "FROM SolicitudRadiografia sr " +
           "JOIN sr.estudiante e " +
           "JOIN Clinica c ON c.rote = e.roteActual " +
           "JOIN sr.radiografia r " +
           "WHERE sr.estado = 'REALIZADO' " +
           "GROUP BY c.idClinica, c.nombreClinica, r.idRadiografia, r.nombreRayo " +
           "ORDER BY c.nombreClinica, COUNT(sr.idSolicitudRadiografia) DESC, r.nombreRayo")
    List<RadiografiaPorClinicaDTO> resumirRadiografiasRealizadasPorClinica();
    
    // =============================================
    // 🔥 MÉTODOS PARA LLAMAR FUNCIONES POSTGRESQL
    // =============================================
    
    @Query(value = "SELECT fn_contar_solicitudes_radiografia_hoy(:idEstudiante)", 
           nativeQuery = true)
    int contarSolicitudesHoy(@Param("idEstudiante") Long idEstudiante);
    
    @Query(value = "SELECT fn_puede_solicitar_radiografia(:idEstudiante)::text", 
           nativeQuery = true)
    String puedeSolicitar(@Param("idEstudiante") Long idEstudiante);
    
    @Query(value = "SELECT fn_verificar_pagos_vencidos_radiografia()", 
           nativeQuery = true)
    String verificarPagosVencidos();
    
    // =============================================
    // 🔥 MÉTODOS EXISTENTES QUE FUNCIONAN
    // =============================================
    
    // Buscar por diagnóstico tratamiento
    List<SolicitudRadiografia> findByDiagnosticoTratamiento_IdDiagTrat(Long idDiagTrat);
    
    // Buscar por estado (Spring Data JPA lo interpreta automáticamente)
    List<SolicitudRadiografia> findByEstado(String estado);
    
    // Buscar por radiografía
    List<SolicitudRadiografia> findByRadiografia_IdRadiografia(Long idRadiografia);
    
    // =============================================
    // 🔥 MÉTODOS CON @Query
    // =============================================
    
    // 🔥 CORREGIDO: Este es el método que causaba el error
    // Usar @Query explícito en lugar de derivado
    @Query("SELECT sr FROM SolicitudRadiografia sr WHERE sr.estado = 'PENDIENTE'")
    List<SolicitudRadiografia> findSolicitudesPendientes();
    
    // Buscar solicitudes por estado y fecha (ordenadas)
    @Query("SELECT sr FROM SolicitudRadiografia sr WHERE sr.estado = :estado ORDER BY sr.fechaSolicitud DESC")
    List<SolicitudRadiografia> findByEstadoOrderByFechaDesc(@Param("estado") String estado);
    
    // Buscar solicitudes de un estudiante
    @Query("SELECT sr FROM SolicitudRadiografia sr " +
           "JOIN sr.diagnosticoTratamiento dt " +
           "JOIN dt.evolucionClinica ec " +
           "JOIN ec.diagnostico d " +
           "JOIN d.revision r " +
           "JOIN r.consulta c " +
           "WHERE c.estudiante.idEstudiante = :idEstudiante")
    List<SolicitudRadiografia> findByEstudianteId(@Param("idEstudiante") Long idEstudiante);
    
    // Buscar solicitudes por estudiante y estado
    @Query("SELECT sr FROM SolicitudRadiografia sr " +
           "WHERE sr.estudiante.idEstudiante = :idEstudiante " +
           "AND sr.estado = :estado")
    List<SolicitudRadiografia> findByEstudianteIdAndEstado(
        @Param("idEstudiante") Long idEstudiante,
        @Param("estado") String estado
    );
    
    // Buscar solicitudes pendientes de pago de un estudiante
    @Query("SELECT sr FROM SolicitudRadiografia sr " +
           "WHERE sr.estudiante.idEstudiante = :idEstudiante " +
           "AND sr.estado = 'APROBADO' " +
           "AND sr.pagado = false")
    List<SolicitudRadiografia> findPendientesPagoByEstudiante(@Param("idEstudiante") Long idEstudiante);
}