package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.ExamenExtraOral;
import java.util.List;
import java.util.Optional;

public interface ExamenExtraOralService {
    
    List<ExamenExtraOral> obtenerTodos();
    
    Optional<ExamenExtraOral> obtenerPorId(Long id);
    
    ExamenExtraOral guardar(ExamenExtraOral examenExtraOral);
    
    void eliminar(Long id);
}