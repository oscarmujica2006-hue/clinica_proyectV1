package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.SolicitudInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudInsumoRepository extends JpaRepository<SolicitudInsumo, Long> {
    
    // Buscar por estado
    List<SolicitudInsumo> findByEstadoSolicitud(String estado);
    
    // Buscar por múltiples estados
    @Query("SELECT s FROM SolicitudInsumo s WHERE s.estadoSolicitud IN :estados")
    List<SolicitudInsumo> findByEstadoSolicitudIn(@Param("estados") List<String> estados);
    
    // ===== NUEVO: Buscar por DiagnosticoTratamiento =====
    List<SolicitudInsumo> findByDiagnosticoTratamiento_IdDiagTrat(Long idDiagTrat);
    
}