package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.DiagnosticoTratamiento;
import java.util.List;
import java.util.Optional;

public interface DiagnosticoTratamientoService {
    
    List<DiagnosticoTratamiento> findAll();
    
    Optional<DiagnosticoTratamiento> obtenerPorId(Long id);
    
    DiagnosticoTratamiento guardar(DiagnosticoTratamiento diagnosticoTratamiento);
    
    void eliminar(Long id);
    
    // Métodos para buscar
    List<DiagnosticoTratamiento> findByEvolucionClinicaId(Long idEvolucionClinica);
    
    List<DiagnosticoTratamiento> findByTratamientoId(Long idTratamiento);
}