package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Informante;
import java.util.List;
import java.util.Optional;

public interface InformanteService {
    
    List<Informante> obtenerTodos();
    
    Optional<Informante> obtenerPorId(Long id);
    
    Informante guardar(Informante informante);
    
    void eliminar(Long id);

}