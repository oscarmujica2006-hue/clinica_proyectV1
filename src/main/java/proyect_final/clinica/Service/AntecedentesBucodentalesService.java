package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.AntecedentesBucodentales;
import java.util.List;
import java.util.Optional;

public interface AntecedentesBucodentalesService {
    
    List<AntecedentesBucodentales> obtenerTodos();
    
    Optional<AntecedentesBucodentales> obtenerPorId(Long id);
    
    AntecedentesBucodentales guardar(AntecedentesBucodentales antecedentesBucodentales);
    
    void eliminar(Long id);
}