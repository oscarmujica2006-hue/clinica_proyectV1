package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.DetalleRecibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleReciboRepository extends JpaRepository<DetalleRecibo, Long> {
    
    // Buscar detalles por recibo
    List<DetalleRecibo> findByRecibo_IdRecibo(Long idRecibo);
    
    // Buscar detalles por tipo de item
    List<DetalleRecibo> findByTipoItem(String tipoItem);
    
    // Buscar detalles por diente del plan
    List<DetalleRecibo> findByDiagnosticoTratamientoDiente_IdDiagnosticoTratamientoDiente(Long idDientePlan);
    
    // Buscar detalles por solicitud de radiografía
    List<DetalleRecibo> findBySolicitudRadiografia_IdSolicitudRadiografia(Long idSolicitud);
}