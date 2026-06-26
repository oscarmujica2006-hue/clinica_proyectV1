package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.DiagnosticoTratamientoDiente;
import java.util.List;
import java.util.Optional;

public interface DiagnosticoTratamientoDienteService {
    
    List<DiagnosticoTratamientoDiente> findAll();
    
    Optional<DiagnosticoTratamientoDiente> obtenerPorId(Long id);
    
    DiagnosticoTratamientoDiente guardar(DiagnosticoTratamientoDiente diente);
    
    List<DiagnosticoTratamientoDiente> guardarTodos(List<DiagnosticoTratamientoDiente> dientes);
    
    void eliminar(Long id);
    
    void eliminarTodosPorDiagnosticoTratamientoId(Long idDiagTrat);
    
    List<DiagnosticoTratamientoDiente> findByDiagnosticoTratamientoId(Long idDiagTrat);
    
    List<DiagnosticoTratamientoDiente> findByDiagnosticoTratamientoIdAndEstado(Long idDiagTrat, String estado);
    
    List<DiagnosticoTratamientoDiente> findByDiente(Integer diente);
    
    long countByDiagnosticoTratamientoId(Long idDiagTrat);
    
    long countByDiagnosticoTratamientoIdAndEstado(Long idDiagTrat, String estado);
    
    // Actualizar estado de un diente
    DiagnosticoTratamientoDiente actualizarEstado(Long idDiente, String nuevoEstado);
    // ✅ AGREGAR ESTE MÉTODO FALTANTE
    void actualizarEstadoPorDiagnosticoTratamientoId(Long idDiagTrat, String nuevoEstado);
}