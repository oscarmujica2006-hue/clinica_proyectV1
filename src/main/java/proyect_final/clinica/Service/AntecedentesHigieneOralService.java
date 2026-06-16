package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.AntecedentesHigieneOral;
import java.util.List;
import java.util.Optional;

public interface AntecedentesHigieneOralService {
    
    List<AntecedentesHigieneOral> obtenerTodos();
    
    Optional<AntecedentesHigieneOral> obtenerPorId(Long id);
    
    AntecedentesHigieneOral guardar(AntecedentesHigieneOral antecedentesHigieneOral);
    
    void eliminar(Long id);
}