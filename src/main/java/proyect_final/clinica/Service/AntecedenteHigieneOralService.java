package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.AntecedenteHigieneOral;
import java.util.List;
import java.util.Optional;

public interface AntecedenteHigieneOralService {
    
    List<AntecedenteHigieneOral> obtenerTodos();
    
    Optional<AntecedenteHigieneOral> obtenerPorId(Long id);
    
    AntecedenteHigieneOral guardar(AntecedenteHigieneOral antecedentesHigieneOral);
    
    void eliminar(Long id);
}