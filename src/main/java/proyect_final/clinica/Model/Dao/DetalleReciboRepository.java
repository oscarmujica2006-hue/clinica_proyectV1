package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.DetalleRecibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleReciboRepository extends JpaRepository<DetalleRecibo, Long> {
    
    // =============================================
    // MÉTODOS BÁSICOS
    // =============================================
    
    // Buscar detalles por recibo
    List<DetalleRecibo> findByRecibo_IdRecibo(Long idRecibo);
    
    // Buscar detalles por tipo de item
    List<DetalleRecibo> findByTipoItem(String tipoItem);
    
    // =============================================
    // MÉTODOS PARA TRIGGERS
    // =============================================
    
    // Buscar detalle por diente plan (para evitar duplicados)
    @Query("SELECT d FROM DetalleRecibo d WHERE d.diagnosticoTratamientoDiente.idDiagnosticoTratamientoDiente = :idDientePlan")
    DetalleRecibo findByDiagnosticoTratamientoDienteId(@Param("idDientePlan") Long idDientePlan);
    
    // Buscar detalle por solicitud de radiografía (para evitar duplicados)
    @Query("SELECT d FROM DetalleRecibo d WHERE d.solicitudRadiografia.idSolicitudRadiografia = :idSolicitud")
    DetalleRecibo findBySolicitudRadiografiaId(@Param("idSolicitud") Long idSolicitud);
    
    // =============================================
    // MÉTODOS DE ESTADÍSTICAS
    // =============================================
    
    // Sumar subtotales por tipo de item
    @Query("SELECT COALESCE(SUM(d.subtotal), 0) FROM DetalleRecibo d WHERE d.recibo.idRecibo = :idRecibo AND d.tipoItem = :tipoItem")
    Double sumSubtotalByTipoItem(@Param("idRecibo") Long idRecibo, @Param("tipoItem") String tipoItem);
}