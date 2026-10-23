package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.DiagnosticoTratamiento;
import proyect_final.clinica.Model.Entity.DiagnosticoTratamientoDiente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiagnosticoTratamientoDienteRepository extends JpaRepository<DiagnosticoTratamientoDiente, Long> {
    
    // ================================================================
    // ============ MÉTODOS EXISTENTES ============
    // ================================================================
    
    // Buscar por ID de DiagnosticoTratamiento
    List<DiagnosticoTratamientoDiente> findByDiagnosticoTratamiento_IdDiagTrat(Long idDiagTrat);
    
    // Buscar por ID de DiagnosticoTratamiento y estado
    List<DiagnosticoTratamientoDiente> findByDiagnosticoTratamiento_IdDiagTratAndEstado(Long idDiagTrat, String estado);
    
    // Buscar por diente específico
    List<DiagnosticoTratamientoDiente> findByDiente(Integer diente);
    
    // Buscar por ID de DiagnosticoTratamiento con orden
    List<DiagnosticoTratamientoDiente> findByDiagnosticoTratamiento_IdDiagTratOrderByDienteAsc(Long idDiagTrat);
    
    // Contar por ID de DiagnosticoTratamiento
    long countByDiagnosticoTratamiento_IdDiagTrat(Long idDiagTrat);
    
    // Contar por ID de DiagnosticoTratamiento y estado
    long countByDiagnosticoTratamiento_IdDiagTratAndEstado(Long idDiagTrat, String estado);
    
    // Obtener todos los dientes de un DiagnosticoTratamiento con detalles
    @Query("SELECT d FROM DiagnosticoTratamientoDiente d " +
           "LEFT JOIN FETCH d.diagnosticoTratamiento dt " +
           "WHERE dt.idDiagTrat = :idDiagTrat")
    List<DiagnosticoTratamientoDiente> findByIdDiagTratWithDetails(@Param("idDiagTrat") Long idDiagTrat);

    // Buscar dientes por plan
    List<DiagnosticoTratamientoDiente> findByDiagnosticoTratamiento(DiagnosticoTratamiento plan);
    
    // Buscar dientes por plan y estado
    List<DiagnosticoTratamientoDiente> findByDiagnosticoTratamientoAndEstado(DiagnosticoTratamiento plan, String estado);
    
    // Buscar dientes por estado
    List<DiagnosticoTratamientoDiente> findByEstado(String estado);

    // ================================================================
    // ============ NUEVOS MÉTODOS PARA EL DOCENTE ============
    // ================================================================

    /**
     * Buscar dientes PENDIENTES de un docente (con consentimiento APROBADO)
     */
    @Query("SELECT dtd FROM DiagnosticoTratamientoDiente dtd " +
           "JOIN dtd.diagnosticoTratamiento dt " +
           "JOIN dt.consentimiento c " +
           "WHERE c.docente.idDocente = :idDocente " +
           "AND c.estado = 'APROBADO' " +
           "AND dtd.estado = 'PENDIENTE_APROBACION' " +
           "ORDER BY dtd.fechRegDiaTraDie ASC")
    List<DiagnosticoTratamientoDiente> findPendientesByDocenteId(@Param("idDocente") Long idDocente);
    
    /**
     * Buscar dientes COMPLETADOS de un docente
     */
    @Query("SELECT dtd FROM DiagnosticoTratamientoDiente dtd " +
           "JOIN dtd.diagnosticoTratamiento dt " +
           "JOIN dt.consentimiento c " +
           "WHERE c.docente.idDocente = :idDocente " +
           "AND c.estado = 'APROBADO' " +
           "AND dtd.estado = 'COMPLETADO' " +
           "ORDER BY dtd.fechModDiaTraDie DESC")
    List<DiagnosticoTratamientoDiente> findCompletadosByDocenteId(@Param("idDocente") Long idDocente);
    
    /**
     * Buscar dientes RECHAZADOS de un docente
     */
    @Query("SELECT dtd FROM DiagnosticoTratamientoDiente dtd " +
           "JOIN dtd.diagnosticoTratamiento dt " +
           "JOIN dt.consentimiento c " +
           "WHERE c.docente.idDocente = :idDocente " +
           "AND c.estado = 'APROBADO' " +
           "AND dtd.estado = 'RECHAZADO' " +
           "ORDER BY dtd.fechModDiaTraDie DESC")
    List<DiagnosticoTratamientoDiente> findRechazadosByDocenteId(@Param("idDocente") Long idDocente);
    
    /**
     * Contar dientes por estado y docente
     */
    @Query("SELECT COUNT(dtd) FROM DiagnosticoTratamientoDiente dtd " +
           "JOIN dtd.diagnosticoTratamiento dt " +
           "JOIN dt.consentimiento c " +
           "WHERE c.docente.idDocente = :idDocente " +
           "AND c.estado = 'APROBADO' " +
           "AND dtd.estado = :estado")
    Long countByEstadoAndDocenteId(@Param("estado") String estado, @Param("idDocente") Long idDocente);
    
    // ================================================================
    // ============ MÉTODOS ADICIONALES ÚTILES ============
    // ================================================================

    /**
     * Buscar dientes PENDIENTES de un docente con todos los datos cargados (evita Lazy Loading)
     */
    @Query("SELECT dtd FROM DiagnosticoTratamientoDiente dtd " +
           "JOIN FETCH dtd.diagnosticoTratamiento dt " +
           "JOIN FETCH dt.tratamiento t " +
           "JOIN FETCH dt.evolucionClinica ec " +
           "JOIN FETCH ec.diagnostico d " +
           "JOIN FETCH d.revision r " +
           "JOIN FETCH r.consulta con " +
           "JOIN FETCH con.paciente p " +
           "JOIN FETCH p.persona per " +
           "JOIN dt.consentimiento c " +
           "WHERE c.docente.idDocente = :idDocente " +
           "AND c.estado = 'APROBADO' " +
           "AND dtd.estado = 'PENDIENTE_APROBACION'")
    List<DiagnosticoTratamientoDiente> findPendientesByDocenteIdWithData(@Param("idDocente") Long idDocente);
    
    /**
     * Contar dientes pendientes de un docente
     */
    @Query("SELECT COUNT(dtd) FROM DiagnosticoTratamientoDiente dtd " +
           "JOIN dtd.diagnosticoTratamiento dt " +
           "JOIN dt.consentimiento c " +
           "WHERE c.docente.idDocente = :idDocente " +
           "AND c.estado = 'APROBADO' " +
           "AND dtd.estado = 'PENDIENTE_APROBACION'")
    Long countPendientesByDocenteId(@Param("idDocente") Long idDocente);
    
    /**
     * Contar dientes completados de un docente
     */
    @Query("SELECT COUNT(dtd) FROM DiagnosticoTratamientoDiente dtd " +
           "JOIN dtd.diagnosticoTratamiento dt " +
           "JOIN dt.consentimiento c " +
           "WHERE c.docente.idDocente = :idDocente " +
           "AND c.estado = 'APROBADO' " +
           "AND dtd.estado = 'COMPLETADO'")
    Long countCompletadosByDocenteId(@Param("idDocente") Long idDocente);
}