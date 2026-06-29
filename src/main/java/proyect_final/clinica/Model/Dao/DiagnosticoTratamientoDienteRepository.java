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

}