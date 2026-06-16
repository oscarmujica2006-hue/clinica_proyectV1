package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.ExamenIntraOral;
import java.util.List;
import java.util.Optional;

public interface ExamenIntraOralService {
    
    List<ExamenIntraOral> obtenerTodos();
    
    Optional<ExamenIntraOral> obtenerPorId(Long id);
    
    ExamenIntraOral guardar(ExamenIntraOral examenIntraOral);
    
    void eliminar(Long id);
}