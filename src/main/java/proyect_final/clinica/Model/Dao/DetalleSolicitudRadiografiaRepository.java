package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.DetalleSolicitudRadiografia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleSolicitudRadiografiaRepository extends JpaRepository<DetalleSolicitudRadiografia, Long> {
    
    // Buscar por solicitud
    List<DetalleSolicitudRadiografia> findBySolicitudRadiografia_IdSolicitudRadiografia(Long idSolicitud);
    
    // Buscar por diente del plan
    List<DetalleSolicitudRadiografia> findByDiagTratDiente_IdDiagnosticoTratamientoDiente(Long idDientePlan);
    
    // Buscar detalles con solicitud
    @Query("SELECT dsr FROM DetalleSolicitudRadiografia dsr " +
           "JOIN FETCH dsr.solicitudRadiografia sr " +
           "WHERE sr.idSolicitudRadiografia = :idSolicitud")
    List<DetalleSolicitudRadiografia> findByIdSolicitudWithDetails(@Param("idSolicitud") Long idSolicitud);
}