package proyect_final.clinica.Model.Dao;

import proyect_final.clinica.Model.Entity.DetalleEvolucionClinica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleEvolucionClinicaRepository extends JpaRepository<DetalleEvolucionClinica, Long> {
    
    // Buscar detalles por evolución clínica
    List<DetalleEvolucionClinica> findByEvolucionClinica_IdEvolucionClinica(Long idEvolucionClinica);
    
    // Buscar detalles por diente del plan
    List<DetalleEvolucionClinica> findByDiagnosticoTratamientoDiente_IdDiagnosticoTratamientoDiente(Long idDientePlan);
    
    // Buscar por evolución y estado (usar estDetEvoClin en lugar de estado)
    List<DetalleEvolucionClinica> findByEvolucionClinica_IdEvolucionClinicaAndEstDetEvoClin(Long idEvolucionClinica, String estDetEvoClin);
    
    // ✅ CORREGIDO: Usar estDetEvoClin en lugar de estado
    List<DetalleEvolucionClinica> findByDiagnosticoTratamientoDiente_DienteAndEstDetEvoClin(Integer diente, String estDetEvoClin);
    
    // Contar cuántos dientes se han trabajado en una evolución
    long countByEvolucionClinica_IdEvolucionClinica(Long idEvolucionClinica);
    
    // Obtener todos los detalles con sus relaciones
    @Query("SELECT d FROM DetalleEvolucionClinica d " +
           "LEFT JOIN FETCH d.evolucionClinica ec " +
           "LEFT JOIN FETCH d.diagnosticoTratamientoDiente dtd " +
           "WHERE d.evolucionClinica.idEvolucionClinica = :idEvolucion")
    List<DetalleEvolucionClinica> findByIdEvolucionWithDetails(@Param("idEvolucion") Long idEvolucion);
}