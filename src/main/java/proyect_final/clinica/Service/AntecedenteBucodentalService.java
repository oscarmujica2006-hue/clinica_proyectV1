package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.AntecedenteBucodental;
import java.util.List;
import java.util.Optional;

public interface AntecedenteBucodentalService {
    
    List<AntecedenteBucodental> obtenerTodos();
    
    Optional<AntecedenteBucodental> obtenerPorId(Long id);
    
    AntecedenteBucodental guardar(AntecedenteBucodental antecedentesBucodentales);
    
    void eliminar(Long id);
}