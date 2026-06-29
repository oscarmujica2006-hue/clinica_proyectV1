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
    
    // Buscar pagos por recibo
    List<PagoRecibo> findByRecibo_IdRecibo(Long idRecibo);
    
    // Buscar pagos por método de pago
    List<PagoRecibo> findByMetodoPago(String metodoPago);
    
    // Buscar pagos por rango de fechas
    @Query("SELECT p FROM PagoRecibo p WHERE p.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    List<PagoRecibo> findPagosByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                              @Param("fechaFin") LocalDateTime fechaFin);
    
    // Sumar total de pagos en un rango de fechas
    @Query("SELECT COALESCE(SUM(p.monto), 0) FROM PagoRecibo p WHERE p.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    Double sumPagosByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                   @Param("fechaFin") LocalDateTime fechaFin);
}