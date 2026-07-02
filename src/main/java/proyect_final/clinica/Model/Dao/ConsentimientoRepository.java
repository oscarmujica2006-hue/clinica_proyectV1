package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.Consentimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentimientoRepository extends JpaRepository<Consentimiento, Long> {
    
    // ===== BÚSQUEDAS BÁSICAS =====
    
    /**
     * Buscar consentimientos por ID del docente
     */
    List<Consentimiento> findByDocente_IdDocente(Long idDocente);
    
    /**
     * Buscar consentimientos por ID del estudiante
     */
    List<Consentimiento> findByEstudiante_IdEstudiante(Long idEstudiante);
    
    /**
     * Buscar consentimientos por estado
     */
    List<Consentimiento> findByEstado(String estado);
    
    /**
     * Buscar consentimientos por docente y estado
     */
    List<Consentimiento> findByDocente_IdDocenteAndEstado(Long idDocente, String estado);
    
    /**
     * Buscar consentimientos por estudiante y estado
     */
    List<Consentimiento> findByEstudiante_IdEstudianteAndEstado(Long idEstudiante, String estado);
    
    /**
     * Buscar consentimiento por ID de DiagnosticoTratamiento
     */
    Optional<Consentimiento> findByDiagnosticoTratamiento_IdDiagTrat(Long idDiagTrat);
    
    /**
     * Contar consentimientos por docente y estado
     */
    Long countByDocente_IdDocenteAndEstado(Long idDocente, String estado);
    
    // ===== CONSULTAS JPQL PERSONALIZADAS =====
    
    /**
     * Buscar consentimientos con datos del paciente
     */
    @Query("SELECT c FROM Consentimiento c " +
           "LEFT JOIN FETCH c.diagnosticoTratamiento dt " +
           "LEFT JOIN FETCH dt.evolucionClinica ec " +
           "LEFT JOIN FETCH ec.diagnostico d " +
           "LEFT JOIN FETCH d.revision r " +
           "LEFT JOIN FETCH r.consulta con " +
           "LEFT JOIN FETCH con.paciente p " +
           "LEFT JOIN FETCH p.persona per " +
           "WHERE c.docente.idDocente = :idDocente")
    List<Consentimiento> findWithPacienteDataByDocenteId(@Param("idDocente") Long idDocente);
    
    /**
     * Buscar consentimientos pendientes de un docente
     */
    @Query("SELECT c FROM Consentimiento c " +
           "WHERE c.docente.idDocente = :idDocente " +
           "AND c.estado = 'PENDIENTE' " +
           "ORDER BY c.fecha ASC")
    List<Consentimiento> findPendientesByDocenteId(@Param("idDocente") Long idDocente);
    
    /**
     * Buscar consentimientos aprobados de un docente
     */
    @Query("SELECT c FROM Consentimiento c " +
           "WHERE c.docente.idDocente = :idDocente " +
           "AND c.estado = 'APROBADO' " +
           "ORDER BY c.fecha DESC")
    List<Consentimiento> findAprobadosByDocenteId(@Param("idDocente") Long idDocente);
    
    /**
     * Buscar consentimientos por rango de fechas
     */
    @Query("SELECT c FROM Consentimiento c " +
           "WHERE c.docente.idDocente = :idDocente " +
           "AND c.fecha BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY c.fecha DESC")
    List<Consentimiento> findByDocenteIdAndFechaBetween(
        @Param("idDocente") Long idDocente,
        @Param("fechaInicio") java.time.LocalDateTime fechaInicio,
        @Param("fechaFin") java.time.LocalDateTime fechaFin
    );
}