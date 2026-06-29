package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.SolicitudRadiografia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRadiografiaRepository extends JpaRepository<SolicitudRadiografia, Long> {
    
    // Buscar por diagnóstico tratamiento
    List<SolicitudRadiografia> findByDiagnosticoTratamiento_IdDiagTrat(Long idDiagTrat);
    
    // Buscar por estado
    List<SolicitudRadiografia> findByEstado(String estado);
    
    // Buscar por radiografía
    List<SolicitudRadiografia> findByRadiografia_IdRadiografia(Long idRadiografia);
    
    // Buscar solicitudes pendientes
    @Query("SELECT sr FROM SolicitudRadiografia sr WHERE sr.estado = 'PENDIENTE'")
    List<SolicitudRadiografia> findSolicitudesPendientes();
    
    // Buscar solicitudes por estado y fecha
    @Query("SELECT sr FROM SolicitudRadiografia sr WHERE sr.estado = :estado ORDER BY sr.fechaSolicitud DESC")
    List<SolicitudRadiografia> findByEstadoOrderByFechaDesc(@Param("estado") String estado);
    
    // Buscar solicitudes de un estudiante (a través de DiagnosticoTratamiento)
    @Query("SELECT sr FROM SolicitudRadiografia sr " +
           "JOIN sr.diagnosticoTratamiento dt " +
           "JOIN dt.evolucionClinica ec " +
           "JOIN ec.diagnostico d " +
           "JOIN d.revision r " +
           "JOIN r.consulta c " +
           "WHERE c.estudiante.idEstudiante = :idEstudiante")
    List<SolicitudRadiografia> findByEstudianteId(@Param("idEstudiante") Long idEstudiante);
}