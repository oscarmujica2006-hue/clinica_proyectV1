package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.PagoRecibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagoReciboRepository extends JpaRepository<PagoRecibo, Long> {
    
    // =============================================
    // MÉTODOS BÁSICOS
    // =============================================
    
    // Buscar pagos por recibo
    @Query("SELECT p FROM PagoRecibo p WHERE p.recibo.idRecibo = :idRecibo")
    List<PagoRecibo> findByReciboId(@Param("idRecibo") Long idRecibo);
    
    // Buscar pagos por rango de fechas
    @Query("SELECT p FROM PagoRecibo p WHERE p.fechRegPago BETWEEN :inicio AND :fin")
    List<PagoRecibo> findByFechRegPagoBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // =============================================
    // MÉTODOS DE ESTADÍSTICAS
    // =============================================
    
    // Sumar montos por fecha
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoRecibo p WHERE p.fechRegPago BETWEEN :inicio AND :fin")
    Double sumMontoByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // Contar pagos por método
    @Query("SELECT COUNT(p) FROM PagoRecibo p WHERE p.metodoPago = :metodoPago")
    Long countByMetodoPago(@Param("metodoPago") String metodoPago);
}