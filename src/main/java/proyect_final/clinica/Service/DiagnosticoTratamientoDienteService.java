package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.DiagnosticoTratamientoDiente;
import java.util.List;
import java.util.Optional;

public interface DiagnosticoTratamientoDienteService {
    
    // ===== MÉTODOS BÁSICOS =====
    List<DiagnosticoTratamientoDiente> findAll();
    
    Optional<DiagnosticoTratamientoDiente> obtenerPorId(Long id);
    
    DiagnosticoTratamientoDiente guardar(DiagnosticoTratamientoDiente diente);
    
    List<DiagnosticoTratamientoDiente> guardarTodos(List<DiagnosticoTratamientoDiente> dientes);
    
    void eliminar(Long id);
    
    // ===== MÉTODOS DE BÚSQUEDA =====
    List<DiagnosticoTratamientoDiente> findByDiagnosticoTratamientoId(Long idDiagTrat);
    
    List<DiagnosticoTratamientoDiente> findByEstado(String estado);
    
    long countByDiagnosticoTratamientoId(Long idDiagTrat);
    
    long countByDiagnosticoTratamientoIdAndEstado(Long idDiagTrat, String estado);
    
    // ===== NUEVOS MÉTODOS PARA EL DOCENTE =====
    
    /**
     * Buscar dientes PENDIENTES de un docente 
     * (con consentimiento APROBADO)
     */
    List<DiagnosticoTratamientoDiente> findPendientesByDocenteId(Long idDocente);
    
    /**
     * Buscar dientes COMPLETADOS de un docente
     */
    List<DiagnosticoTratamientoDiente> findCompletadosByDocenteId(Long idDocente);
    
    /**
     * Buscar dientes RECHAZADOS de un docente
     */
    List<DiagnosticoTratamientoDiente> findRechazadosByDocenteId(Long idDocente);
    
    /**
     * Contar dientes por estado y docente
     */
    Long countByEstadoAndDocenteId(String estado, Long idDocente);
}