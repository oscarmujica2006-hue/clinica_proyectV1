package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.EncargadoInsumo;
import proyect_final.clinica.Model.Entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface EncargadoInsumoService {
    
    List<EncargadoInsumo> findAll();
    
    Optional<EncargadoInsumo> findById(Long id);
    
    EncargadoInsumo save(EncargadoInsumo encargadoInsumo);
    
    void deleteById(Long id);
    
    Optional<EncargadoInsumo> findByUsuario(Usuario usuario);
    
    Optional<EncargadoInsumo> findByUsuarioId(Long idUsuario);
    
    boolean existsById(Long id);
    
    long count();
}