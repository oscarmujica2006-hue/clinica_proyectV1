package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.Radiografia;
import java.util.List;
import java.util.Optional;

public interface RadiografiaService {
    
    List<Radiografia> listarTodos();
    
    Optional<Radiografia> obtenerPorId(Long id);
    
    Radiografia guardar(Radiografia radiografia);
    
    void eliminar(Long id);
    
    List<Radiografia> buscarPorNombre(String nombre);
}