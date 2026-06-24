package proyect_final.clinica.Service;

import proyect_final.clinica.Model.Entity.UnidadMedida;
import java.util.List;
import java.util.Optional;

public interface UnidadMedidaService {
    List<UnidadMedida> findAll();
    Optional<UnidadMedida> findById(Long id);
    UnidadMedida save(UnidadMedida unidadMedida);
    void deleteById(Long id);
}